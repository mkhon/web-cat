#========================================================================
package Web_CAT::JUnitResultsReader;
#========================================================================
use warnings;
use strict;
use Carp;
use File::stat;

#========================================================================
#                      -----  PUBLIC METHODS -----
#========================================================================

#========================================================================
sub new
{
    my $proto = shift;
    my $class = ref( $proto )  ||  $proto;
    my $self = {
        'hints'      => [{}, {}, {}],
        'plist'      => "",
        'executed'   => 0.0,
        'failed'     => 0.0,
        'hasResults' => 0,
        'hintId'     => 0
    };
    my $fileName = shift;
    bless( $self, $class );
    if ( defined( $fileName ) )
    {
        $self->parseFile( $fileName );
    }
    return $self;
}


#========================================================================
sub parseFile
{
    my $self = shift;
    my $fileName = shift || croak( "file name required" );
    # print "attempting to parse '$fileName'\n";
    if ( -f $fileName && stat( $fileName )->size > 0 )
    {
        local *FH;
        open( FH, "$fileName" )
            or croak "Cannot open '$fileName' for reading: $!";
        # Slurp the whole file
        my @lines = <FH>;
        close( FH );
        my $results = $self;
        eval( join( "", @lines ) );
        carp $@ if $@;
        $self->{'hasResults'} = 1;
    }
    else
    {
        $self->{'hasResults'} = 0;
    }
}


#========================================================================
# addHint( mandatory, hint, trace );
sub addHint
{
    my $self     = shift;
    my $category = shift;
    my $hint     = shift || croak( "hint text required" );
    my $trace    = shift;
    croak( "category required" ) if !defined( $category );

#    print "addHint( $category, \"$hint\", ... );\n";
    my $hintRecord;
    if ( !exists( $self->{'hints'}->[$category]->{$hint} ) )
    {
        # print "adding new hint record\n";
        $hintRecord = {
            'text'  => $hint,
            'count' => 0,
            'id'    => ++$self->{'hintId'}
        };
        if ( defined( $trace ) )
        {
            $hintRecord->{'trace'} = $trace;
        }
        $self->{'hints'}->[$category]->{$hint} = $hintRecord;
    }
    else
    {
        $hintRecord = $self->{'hints'}->[$category]->{$hint};
    }
    $hintRecord->{'count'}++;
    if ( defined( $trace ) && !exists( $hintRecord->{'trace'} ) )
    {
        $hintRecord->{'trace'} = $trace;
    }
#    {
#       my $trace = "";
#       if ( exists( $hintRecord->{'trace'} ) )
#       {
#           $trace = $hintRecord->{'trace'};
#       }
#       print "hint: ", $hintRecord->{'text'}, "\n\tcount = ",
#           $hintRecord->{'count'}, "\n\ttrace = ", $trace, "\n\n";
#    }
}


#========================================================================
# addTestsExecuted( dbl );
sub addTestsExecuted
{
    my $self = shift;
    my $amt = shift;
    croak( "amount required" ) unless defined( $amt );
    $self->{'executed'} += $amt;
}


#========================================================================
# addTestsFailed( dbl );
sub addTestsFailed
{
    my $self = shift;
    my $amt = shift;
    croak( "amount required" ) unless defined( $amt );
    $self->{'failed'} += $amt;
}


#========================================================================
# addToPlist( plist );
sub addToPlist
{
    my $self = shift;
    my $add = shift;
    while ( defined $add )
    {
        chomp $add;
        $self->{'plist'} .= $add;
        $add = shift;
    }
}


#========================================================================
sub testsExecuted
{
    my $self = shift;
    return $self->{'executed'};
}


#========================================================================
sub testsFailed
{
    my $self = shift;
    return $self->{'failed'};
}


#========================================================================
sub hasResults
{
    my $self = shift;
    return $self->{'hasResults'};
}


#========================================================================
sub testPassRate
{
    my $self = shift;
    my $result = 0;
    if ( $self->testsExecuted > 0 )
    {
        $result = ( $self->testsExecuted - $self->testsFailed )
            / $self->testsExecuted;
    }
    return $result;
}


#========================================================================
sub allTestsPass
{
    my $self = shift;
    return ( $self->testsExecuted > 0 )  &&  ( $self->testsFailed == 0 );
}


#========================================================================
sub allTestsFail
{
    my $self = shift;
    return ( $self->testsExecuted > 0 )
        && ( $self->testsFailed == $self->testsExecuted );
    
}


#========================================================================
sub plist
{
    my $self = shift;
    my $result = $self->{'plist'};
    $result =~ s/,$//o;
    return "(" . $result . ")";
}


#========================================================================
# formatHints( category, limit );
sub formatHints
{
    my $self     = shift;
    my $category = shift || 0;
    my $limit    = shift;
    my $result   = undef;

    if ( !defined( $limit ) ) { $limit = -1; }

    my @hints = sort
        { ( $a->{'count'} <=> $b->{'count'} )
          || ( $b->{'id'} <=> $a->{'id'} ) }
        values %{$self->{'hints'}->[$category]};

    my $total = $#hints + 1;
    if ( $limit < 0 || $limit > $total )
    {
        $limit = $total;
    }

    if ( $category == 0 )
    {
        my @mandatory = sort
        { ( $a->{'count'} <=> $b->{'count'} )
          || ( $b->{'id'} <=> $a->{'id'} ) }
            values %{$self->{'hints'}->[$category + 1]};
        $limit += $#mandatory + 1;
        @hints= (@hints, @mandatory);
    }

    my $shown = $limit;

#    print "sorted hints:\n";
#    foreach my $hint ( @hints )
#    {
#       my $trace = "";
#       if ( exists( $hint->{'trace'} ) )
#       {
#           $trace = $hint->{'trace'};
#       }
#       print "hint: ", $hint->{'text'}, "\n\tcount = ",
#           $hint->{'count'}, "\n\ttrace = ", $trace, "\n\n";
#    }

    if ( $limit > 0 && $#hints >= 0 )
    {
        $result = "<ul>";
        while ( $limit >0 && $#hints >= 0 )
        {
            my $hint = pop( @hints );
            $result .= "<li><p>" . $hint->{'text'};
            if ( $hint->{'count'} > 1 )
            {
                $result .= " (" . $hint->{'count'} . " occurrences)";
            }
            $result .= "</p>\n";
            if ( defined $hint->{'trace'} )
            {
                $result .= "<pre>\n" . $hint->{'trace'} . "</pre>\n";
            }
            $result .= "</li>";

            $limit--;
        }
        $result .= "</ul>";
        if ( $#hints >= 0 )
        {
            $result .= "<p>(only $shown of $total hints shown)</p>";
        }
    }

    return $result;
}


# ---------------------------------------------------------------------------
1;
# ---------------------------------------------------------------------------
