#========================================================================
package Web_CAT::Utilities;

=head1 NAME

Web_CAT::Utilities - utility functions for Web-CAT grading plug-ins

=head1 SYNOPSIS

  use Web_CAT::Utilities qw( confirmExists copyHere addReportFile );

  my $fileName = confirmExists( $baseDir, $subpath );
  copyHere( $fileName, $baseDir );
  addReportFile( $subpath, "text/plain" );

=head1 DESCRIPTION

This module provides a number of top-level functions that may be
useful in writing Web-CAT grading plug-ins.

=cut

#========================================================================
use warnings;
use strict;
use File::stat;
use File::Copy;
use File::Glob qw(bsd_glob);
use Web_CAT::HTML::Entities;
use Text::Tabs;
use Carp qw( carp confess );
use vars qw( @ISA @EXPORT_OK $PATH_SEPARATOR $FILE_SEPARATOR $SHELL
             %WINDOWS_1252_EXTENDED );
use Exporter qw( import );

@ISA = qw( Exporter );
@EXPORT_OK = qw( initFromConfig
                 confirmExists filePattern copyHere htmlEscape addReportFile
                 addReportFileWithStyle scanTo scanThrough printableSize
                 linesFromFile ltrim rtrim trim rawToUtf8 loadFileAsUtf8
                 $PATH_SEPARATOR
                 $FILE_SEPARATOR
                 $SHELL
                 $WINDOWS_1252_EXTENDED
                 );

$PATH_SEPARATOR = ':';
$FILE_SEPARATOR = '/';
$SHELL = '';

my %shells = (
    'MSWin32' => ['cmd.exe /c ', '\\', ';'],
    'dos'     => ['cmd.exe /c ', '\\', ';'],
    'NetWare' => ['cmd.exe /c ', '\\', ';']
);


#========================================================================
#                      -----  INITIALIZATION -----
#========================================================================
INIT
{
    if ( defined $shells{$^O})
    {
        ($SHELL, $FILE_SEPARATOR, $PATH_SEPARATOR) = @{$shells{$^O}};
    }
}


#========================================================================
#                      -----  PUBLIC METHODS -----
#========================================================================

#========================================================================
sub initFromConfig
{

=head2 initFromConfig($cfg)

Pulls any overriding definitions of exported vars from supplied
config file.

=cut

    my $cfg = shift || confess "initFromConfig: cfg object required";

    $PATH_SEPARATOR = $cfg->getProperty(
        'PerlForPlugins.path.separator', $PATH_SEPARATOR );
    $FILE_SEPARATOR = $cfg->getProperty(
        'PerlForPlugins.file.separator', $FILE_SEPARATOR );
}


#========================================================================
sub confirmExists
{

=head2 confirmExists($baseDir, $subpath)

Checks a file or path name to ensure it exists, and returns the
resulting full path name.  The first parameter is a directory to
look in, and the (optional) second parameter is a relative sub-path
to look for.  If the given destination exists (as a file or directory),
then "$baseDir/$subpath" is returned.  Optionally, when invoked with
just a single argument, this function assumes that the lone argument
is the full path name to check for.  In either case, if the given
destination does not exist, the function dies via confess.

=cut

    my $baseDir = shift || carp "confirmExists: baseDir required";
    my $subpath = shift;
    my $target = $baseDir;
    if ( defined $subpath )
    {
        $target .= "/$subpath";
    }
    confess "required user data file/dir $target does not exist"
        unless ( -e $target );
    return $target;
}


#========================================================================
sub filePattern
{

=head2 filePattern($path)

Converts a file path to a corresponding regular expression pattern.
The pattern matches both all-forward-slash and all-backward-slash
versions of the path.  We cannot simply rely on the Perl OS build
information ($^O) to determine the correct pattern to use, since
the choice of slashes to use in various parts of the ANT log may
be made by various external programs (e.g., even under a cygwin
build of Perl, we must support both forward and backward slashes
in the ANT log, since other tools may be pure Win32 executables).

=cut

    my $path = shift || carp "filePattern: path required";
    if ( $path !~ m,/,o )
    {
        $path =~ s,\\,/,go;
    }
    $path =~ s,//+,/,go;
    my $dosPath = $path;
    $dosPath =~ s,/,\\,go;
    return qr/\Q$path\E|\Q$dosPath\E/;
}


#========================================================================
sub copyHere
{

=head2 copyHere($pathName, $prefixDirToStrip, $ignoreList)

Recursively copy a directory's contents (or a single file) to the
current working directory.  Recursively traverses the directory
structure rooted at $pathName, and copies each file to a local relative
path name obtained by stripping the leading $prefixDirToStrip portion
of the directory name.  The third (optional) parameter, if given,
should be a reference to an array.  If provided, each file copied
will be added to the corresponding array.

=cut

   # ' <- for emacs Perl mode

    my $file = shift || carp "copyHere: pathName required";
    my $base = shift || carp "copyHere: prefixDirToStrip required";
    my $ignoreList = shift;
    $base =~ s,/$,,o;
    my $newfile = $file;
    $newfile =~ s,^\Q$base/\E,,;
    if (-d $file)
    {
        if ($file ne $base)
        {
            # print "mkdir( $newfile );\n" if $debug;
            mkdir($newfile);
        }
        foreach my $f (bsd_glob("$file/*"))
        {
            if ($f =~ m,/(\.gitignore|__MAC_OSX|CVS),o)
            {
                next;
            }
            copyHere($f, $base, $ignoreList);
        }
    }
    else
    {
        # print "copy($file, $newfile);\n" if $debug;
        copy($file, $newfile);
        if (defined $ignoreList)
        {
            push(@{$ignoreList}, $newfile);
        }
    }
}


#========================================================================
sub htmlEscape
{

=head2 htmlEscape($string)

Uses Text::Tabs::expand() to expand tabs, and then uses
HTML::Entities::encode_entities() to escape any HTML control
characters, returning the final result.

=cut

    my $str = shift;
    if ( defined $str )
    {
        $str = Web_CAT::HTML::Entities::encode_entities_numeric(
            expand($str), '^\n\r\t !\#\$%\(-;=?-~');
        $str =~ s/&#([01]?[0-9A-Fa-f]);/&#171;&amp;#$1&#187;/g;
    }
    return $str;
}


#========================================================================
sub addReportFile
{
    my $cfg          = shift || confess "addReportFile: cfg object required";
    my $file         = shift || confess "addReportFile: file name required";
    my $mimeType     = shift || "text/html";
    my $to           = shift;
    my $inline       = shift;
    my $label        = shift;

    addReportFileWithStyle($cfg, $file, $mimeType, 0, $to, $inline, $label);
}


#========================================================================
sub addReportFileWithStyle
{

=head2 addReportFile($cfg, $file, $mimeType, $styleVersion, $to, $inline, $label)

Adds a specified report file to the specified properties file, and
update the "numReports" property appropriately.
The parameters are:

=over

=item $cfg (required)

A reference to a Config::Properties object containing the property
settings to change.

=item $file (required)

The relative pathname of the report file to add.

=item $mimeType (optional)

The mime type to record for the given file.  If omitted, the default is
"text/html".

=item $styleVersion (optional)

Use the specified style version for collapsible inline report sections.
0 = legacy styles, 1 = Dojo title panes.

=item $to (optional)

Set to "admin" to target a report for administrators.  The default is
"student".

=item $inline (optional)

If true, the report should be shown inline.  If false, the report should
instead be presented for download instead of shown inline.

=item $label (optional)

The label/description to use for downloaded (rather than inline) files.

=back

=cut

    my $cfg          = shift || confess "addReportFile: cfg object required";
    my $file         = shift || confess "addReportFile: file name required";
    my $mimeType     = shift || "text/html";
    my $styleVersion = shift || 0;
    my $to           = shift;
    my $inline       = shift;
    my $label        = shift;

    my $numReports = $cfg->getProperty( 'numReports', 0 );
    $numReports++;

    $cfg->setProperty( "report${numReports}.file",     $file );
    $cfg->setProperty( "report${numReports}.mimeType", $mimeType );
    if ( $styleVersion )
    {
        $cfg->setProperty( "report${numReports}.styleVersion", $styleVersion );
    }
    if ( defined $to )
    {
        $cfg->setProperty( "report${numReports}.to", $to );
    }
    if ( defined $inline )
    {
        $cfg->setProperty( "report${numReports}.inline", $inline );
    }
    if ( defined $label )
    {
        $cfg->setProperty( "report${numReports}.label", $label );
    }
    $cfg->setProperty( 'numReports', $numReports );
}


#========================================================================
sub scanTo
{

=head2 scanTo($pattern, $fileHandle)

Using the global $_ variable as a line buffer, reads from the
specified file handle until a line matching the given regular
expression is found.  No read will be performed if $_ already
matches the pattern.  The file handle is optional, and defaults
to *main::ANTLOG{IO}.

=cut

    my $pattern = shift || '^\s*$';
    my $fh      = shift || *main::ANTLOG{IO};

    if ( defined( $_ ) && !( m/$pattern/ ) )
    {
        while ( <$fh> )
        {
            last if m/$pattern/;
        }
    }
}


#========================================================================
sub scanThrough
{

=head2 scanThrough($pattern, $fileHandle)

Using the global $_ variable as a line buffer, reads from the
specified file handle until a non-blank line that does not match
the given regular expression is found.  No read will be performed
if $_ is already non-blank and fails to match the pattern.  The
file handle is optional, and defaults to *main::ANTLOG{IO}.

=cut

    my $pattern = shift || confess "scanThrough: pattern required";
    my $fh      = shift || *main::ANTLOG{IO};

    while (defined($_) && (
        m/^\s*$/o
        || m/$pattern/
        || m/^([Tt]arget )?\S+: (started|finished)/o
        ))
    {
        $_ = <$fh>;
    }
}


#========================================================================
sub printableSize
{

=head2 printableSize($size)

Converts an integer size (in bytes) into a human readable size
(e.g., 50Kb, 1.2Mb, etc.).

=cut

    my $size = shift || confess "printableSize: size required";
    my $result = $size;

    if ( $size < 1024 )
    {
        $result .= " bytes";
    }
    elsif ( $size < 1048576 )
    {
        $result = int( $size / 1024 * 10 ) / 10;
        $result .= "Kb";
    }
    else
    {
        $result = int( $size / 1048576 * 10 ) / 10;
        $result .= "Mb";
    }
    return $result;
}


#========================================================================
sub linesFromFile
{

=head2 linesFromFile($fileName, $sizeLimit, $lines, $isHTML)

Pull lines from a specified file into memory and return them as an
array.  If the file does not exist, returns undef.  If the file exists,
but is larger than $sizeLimit, only the first $lines number of lines
will be returned, rather than the entire file contents.  An HTML-ized
error message indicating the total file size will be added at the end
of the array of lines if the file is too large.  If $sizeLimit is
omitted, a default value of 50K is used.  If $lines is omitted, a
default value of 500 is used.  For large files with long lines, fewer
than $lines lines may be returned, since the function will stop reading
from the input file once it processes more than $sizeLimit bytes.
However, it will not truncate the last line--the full line will be
included in the result.

=cut

    my $fileName  = shift || confess "linesFromFile: fileName required";
    my $sizeLimit = shift || 50000;
    my $lines     = shift || 500;
    my $isHTML    = shift || 0;
    my @result = ();

    # print "linesFromFile: checking file $fileName\n";
    if ( ! -f $fileName )
    {
        return @result;
    }

    my $size = stat( $fileName )->size;
    my $limitLines = stat( $fileName )->size > $sizeLimit;
    # print "linesFromFile: size = $size, limit = $limitLines\n";
    open( LINESFROMFILE, $fileName ) || return undef;
    if ( $size <= $sizeLimit )
    {
        # print "linesFromFile: short case\n";
        @result = <LINESFROMFILE>;
    }
    else
    {
        # print "linesFromFile: long case\n";
        my $line;
        my $lineCount = 0;
        my $resultSize = 0;
        while ( $lineCount < $lines
                && $resultSize < $sizeLimit
                && ( $line = <LINESFROMFILE> ) )
        {
            push( @result, $line );
            $resultSize += length( $line );
            $lineCount++;
        }
        push( @result, "\n" );
        push( @result, ". . .\n" );
        push( @result, "<b class=\"warn\">(Output of "
              . printableSize( $size )
              . " too long, only the first "
              . $lineCount
              . " lines are shown)</b>\n" );
    }
    close( LINESFROMFILE );
    if ( !$isHTML )
    {
        @result = map( htmlEscape( $_ ), @result );
    }
    # print "$#result lines to return\n";
    return @result;
}


#========================================================================
sub ltrim
{

=head2 ltrim($string)

Returns the string with all white space removed from the left
end (front).

=cut

    my $string = shift || confess "ltrim: string required";
    $string =~ s/^\s+//o;
    return $string;
}


#========================================================================
sub rtrim
{

=head2 rtrim($string)

Returns the string with all white space removed from the right
end (back).

=cut

    my $string = shift || confess "rtrim: string required";
    $string =~ s/\s+$//o;
    return $string;
}


#========================================================================
sub trim
{

=head2 trim($string)

Returns the string with all white space removed from both ends.

=cut

    my $string = shift || confess "trim: string required";
    $string =~ s/^\s+//o;  # ltrim
    $string =~ s/\s+$//o;  # rtrim
    return $string;
}


#========================================================================
%WINDOWS_1252_EXTENDED = (
  "\x{80}" => "\x{e2}\x{82}\x{ac}",
  "\x{81}" => "",
  "\x{82}" => "\x{e2}\x{80}\x{9a}",
  "\x{83}" => "\x{c6}\x{92}",
  "\x{84}" => "\x{e2}\x{80}\x{9e}",
  "\x{85}" => "\x{e2}\x{80}\x{a6}",
  "\x{86}" => "\x{e2}\x{80}\x{a0}",
  "\x{87}" => "\x{e2}\x{80}\x{a1}",
  "\x{88}" => "\x{cb}\x{86}",
  "\x{89}" => "\x{e2}\x{80}\x{b0}",
  "\x{8a}" => "\x{c5}\x{a0}",
  "\x{8b}" => "\x{e2}\x{80}\x{b9}",
  "\x{8c}" => "\x{c5}\x{92}",
  "\x{8d}" => "",
  "\x{8e}" => "\x{c5}\x{bd}",
  "\x{8f}" => "",

  "\x{90}" => "",
  "\x{91}" => "\x{e2}\x{80}\x{98}",
  "\x{92}" => "\x{e2}\x{80}\x{99}",
  "\x{93}" => "\x{e2}\x{80}\x{9c}",
  "\x{94}" => "\x{e2}\x{80}\x{9d}",
  "\x{95}" => "\x{e2}\x{80}\x{a2}",
  "\x{96}" => "\x{e2}\x{80}\x{93}",
  "\x{97}" => "\x{e2}\x{80}\x{94}",
  "\x{98}" => "\x{cb}\x{9c}",
  "\x{99}" => "\x{e2}\x{84}\x{a2}",
  "\x{9a}" => "\x{c5}\x{a1}",
  "\x{9b}" => "\x{e2}\x{80}\x{ba}",
  "\x{9c}" => "\x{c5}\x{93}",
  "\x{9d}" => "",
  "\x{9e}" => "\x{c5}\x{be}",
  "\x{9f}" => "\x{c5}\x{b8}",

  "\x{a0}" => "\x{c2}\x{a0}",
  "\x{a1}" => "\x{c2}\x{a1}",
  "\x{a2}" => "\x{c2}\x{a2}",
  "\x{a3}" => "\x{c2}\x{a3}",
  "\x{a4}" => "\x{c2}\x{a4}",
  "\x{a5}" => "\x{c2}\x{a5}",
  "\x{a6}" => "\x{c2}\x{a6}",
  "\x{a7}" => "\x{c2}\x{a7}",
  "\x{a8}" => "\x{c2}\x{a8}",
  "\x{a9}" => "\x{c2}\x{a9}",
  "\x{aa}" => "\x{c2}\x{aa}",
  "\x{ab}" => "\x{c2}\x{ab}",
  "\x{ac}" => "\x{c2}\x{ac}",
  "\x{ad}" => "\x{c2}\x{ad}",
  "\x{ae}" => "\x{c2}\x{ae}",
  "\x{af}" => "\x{c2}\x{af}",

  "\x{b0}" => "\x{c2}\x{b0}",
  "\x{b1}" => "\x{c2}\x{b1}",
  "\x{b2}" => "\x{c2}\x{b2}",
  "\x{b3}" => "\x{c2}\x{b3}",
  "\x{b4}" => "\x{c2}\x{b4}",
  "\x{b5}" => "\x{c2}\x{b5}",
  "\x{b6}" => "\x{c2}\x{b6}",
  "\x{b7}" => "\x{c2}\x{b7}",
  "\x{b8}" => "\x{c2}\x{b8}",
  "\x{b9}" => "\x{c2}\x{b9}",
  "\x{ba}" => "\x{c2}\x{ba}",
  "\x{bb}" => "\x{c2}\x{bb}",
  "\x{bc}" => "\x{c2}\x{bc}",
  "\x{bd}" => "\x{c2}\x{bd}",
  "\x{be}" => "\x{c2}\x{be}",
  "\x{bf}" => "\x{c2}\x{bf}",

  "\x{c0}" => "\x{c3}\x{80}",
  "\x{c1}" => "\x{c3}\x{81}",
  "\x{c2}" => "\x{c3}\x{82}",
  "\x{c3}" => "\x{c3}\x{83}",
  "\x{c4}" => "\x{c3}\x{84}",
  "\x{c5}" => "\x{c3}\x{85}",
  "\x{c6}" => "\x{c3}\x{86}",
  "\x{c7}" => "\x{c3}\x{87}",
  "\x{c8}" => "\x{c3}\x{88}",
  "\x{c9}" => "\x{c3}\x{89}",
  "\x{ca}" => "\x{c3}\x{8a}",
  "\x{cb}" => "\x{c3}\x{8b}",
  "\x{cc}" => "\x{c3}\x{8c}",
  "\x{cd}" => "\x{c3}\x{8d}",
  "\x{ce}" => "\x{c3}\x{8e}",
  "\x{cf}" => "\x{c3}\x{8f}",

  "\x{d0}" => "\x{c3}\x{90}",
  "\x{d1}" => "\x{c3}\x{91}",
  "\x{d2}" => "\x{c3}\x{92}",
  "\x{d3}" => "\x{c3}\x{93}",
  "\x{d4}" => "\x{c3}\x{94}",
  "\x{d5}" => "\x{c3}\x{95}",
  "\x{d6}" => "\x{c3}\x{96}",
  "\x{d7}" => "\x{c3}\x{97}",
  "\x{d8}" => "\x{c3}\x{98}",
  "\x{d9}" => "\x{c3}\x{99}",
  "\x{da}" => "\x{c3}\x{9a}",
  "\x{db}" => "\x{c3}\x{9b}",
  "\x{dc}" => "\x{c3}\x{9c}",
  "\x{dd}" => "\x{c3}\x{9d}",
  "\x{de}" => "\x{c3}\x{9e}",
  "\x{df}" => "\x{c3}\x{9f}",

  "\x{e0}" => "\x{c3}\x{a0}",
  "\x{e1}" => "\x{c3}\x{a1}",
  "\x{e2}" => "\x{c3}\x{a2}",
  "\x{e3}" => "\x{c3}\x{a3}",
  "\x{e4}" => "\x{c3}\x{a4}",
  "\x{e5}" => "\x{c3}\x{a5}",
  "\x{e6}" => "\x{c3}\x{a6}",
  "\x{e7}" => "\x{c3}\x{a7}",
  "\x{e8}" => "\x{c3}\x{a8}",
  "\x{e9}" => "\x{c3}\x{a9}",
  "\x{ea}" => "\x{c3}\x{aa}",
  "\x{eb}" => "\x{c3}\x{ab}",
  "\x{ec}" => "\x{c3}\x{ac}",
  "\x{ed}" => "\x{c3}\x{ad}",
  "\x{ee}" => "\x{c3}\x{ae}",
  "\x{ef}" => "\x{c3}\x{af}",

  "\x{f0}" => "\x{c3}\x{b0}",
  "\x{f1}" => "\x{c3}\x{b1}",
  "\x{f2}" => "\x{c3}\x{b2}",
  "\x{f3}" => "\x{c3}\x{b3}",
  "\x{f4}" => "\x{c3}\x{b4}",
  "\x{f5}" => "\x{c3}\x{b5}",
  "\x{f6}" => "\x{c3}\x{b6}",
  "\x{f7}" => "\x{c3}\x{b7}",
  "\x{f8}" => "\x{c3}\x{b8}",
  "\x{f9}" => "\x{c3}\x{b9}",
  "\x{fa}" => "\x{c3}\x{ba}",
  "\x{fb}" => "\x{c3}\x{bb}",
  "\x{fc}" => "\x{c3}\x{bc}",
  "\x{fd}" => "\x{c3}\x{bd}",
  "\x{fe}" => "\x{c3}\x{be}",
  "\x{ff}" => "\x{c3}\x{bf}"
);

sub rawToUtf8
{

=head2 rawToUtf8($stringRef)

Takes a string consisting of raw octets/bytes and converts it in place
into the corresponding UTF-8 string, similar to utf8::decode().  However,
this function handles non-UTF-8-compliant octets in the range 0x80-0xff
by interpreting them as being Windows-1252-encoded (which subsumes
Latin1/OSI-8859-1 as well).  Always returns with the string transformed
into a valid UTF-8 string with the Perl utf-8 marker set.

=cut

    my $string = shift || confess "rawToUtf8: string required";
    if (!utf8::decode(${ $string }))
    {
        ${ $string } =~ s/
            ([\x{c0}-\x{df}][\x{80}-\x{bf}])
            |([\x{e0}-\x{ef}][\x{80}-\x{bf}][\x{80}-\x{bf}])
            |([\x{f0}-\x{f7}][\x{80}-\x{bf}][\x{80}-\x{bf}][\x{80}-\x{bf}])

            |([\x{80}-\x{ff}])
        /
            (defined $4)
                ? ($WINDOWS_1252_EXTENDED{$4} || '')
                : $&
        /oexg;
        utf8::decode(${ $string });
    }
    return $string;
}


#========================================================================
sub loadFileAsUtf8
{

=head2 loadFileAsUtf8($fileName)

Pull lines from a specified file into memory and return them as an
array of UTF-8 strings.  Files in UTF-8, Windows-1252, ISO-8559-1,
or an improper mix of these encodings will be converted correctly.
Files in other encodings will result in proper UTF-8 results, although
extended characters outside the ASCII range may be misinterpreted.

=cut

    my $fileName = shift || confess "loadFileAsUtf8: fileName required";

    open(FILEIN, '<:raw', $fileName) or return undef;
    my @out = <FILEIN>;
    close(FILEIN);
    if ($#out >= 0)
    {
        # Strip UTF-8 BOM, if any
        # TODO: what about other UTF-x BOMs?
        $out[0] =~ s/^\x{ef}\x{bb}\x{bf}//o;

        # Convert to utf8, if it looks like something else
        @out = map { rawToUtf8(\$_); $_ } @out;

        # Normalize line endings, including old Mac-style
        @out = map {
            $_ =~ s/\r\n/\n/go;
            $_ =~ s/\r/\r\n/go;
            map { $_ =~ s/\r//go; $_ } split(/(?<=\r\n)/, $_)
        } @out;
    }
    return @out;
}


# ---------------------------------------------------------------------------
1;
# ---------------------------------------------------------------------------
__END__

=head1 AUTHOR

Stephen Edwards

$Id: Utilities.pm,v 1.12 2014/06/16 17:45:37 stedwar2 Exp $
