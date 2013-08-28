#========================================================================
package Web_CAT::Beautifier;
#========================================================================
use warnings;
use strict;
use Web_CAT::Beautifier::Core;
use File::Path;
use Web_CAT::HFile::HFile_ascii;
use Web_CAT::Output::HTML;
use vars qw( @ISA %extensionMap );
use Carp;

@ISA = qw( Web_CAT::Beautifier::Core );

my %icons = (
    "Error"        => "http://web-cat.org/icons/exclaim.gif",
    "Warning"      => "http://web-cat.org/icons/caution.gif",
    "Question"     => "http://web-cat.org/icons/help.gif",
    "Suggestion"   => "http://web-cat.org/icons/suggestion.gif",
    "Answer"       => "http://web-cat.org/icons/answer.gif",
    "Good"         => "http://web-cat.org/icons/check.gif",
    "Extra Credit" => "http://web-cat.org/icons/excred.gif",
    "default"      => "http://web-cat.org/icons/todo.gif"
);

my %categoryPriority = (
    "Error"        => 1,
    "Warning"      => 2,
    "Question"     => 3,
    "Suggestion"   => 4,
    "Answer"       => 5,
    "Good"         => 6,
    "Extra Credit" => 7
);

%extensionMap = (
    "ascii"  => "ascii",
    "cpp"    => "cpp",
    "csv"    => "ascii",
    "cxx"    => "cpp",
    "g"      => "ANTLR",
    "h"      => "cpp",
    "hpp"    => "cpp",
    "hs"     => "haskell",
    "htm"    => "html",
    "html"   => "html",
    "input"  => "ascii",
    "java"   => "java",
    "pas"    => "pascal",
    "pascal" => "pascal",
    "pl"     => "prolog",
    "plg"    => "prolog",
    "pp"     => "pascal",
    "pro"    => "prolog",
    "prolog" => "prolog",
    "py"     => "python",
    "python" => "python",
    "readme" => "ascii",
    "rss"    => "xml",
    "rkt"    => "scheme",
    "scm"    => "scheme",
    "scheme" => "scheme",
    "script" => "ascii",
    "ss"     => "scheme",
    "txt"    => "ascii",
    "text"   => "ascii",
    "xml"    => "xml",
    "xul"    => "xml",
    "zhtml"  => "html",
    "zul"    => "xml"
    );


#========================================================================
#                      -----  PUBLIC METHODS -----
#========================================================================

#========================================================================
sub new
{
    my $proto = shift;
    my $class = ref( $proto )  ||  $proto;
    my $self = $class->SUPER::new( new Web_CAT::HFile::HFile_ascii,
                                   new Web_CAT::Output::HTML );
    $self->{language} = "ascii";
    $self->{countLoc} = 0;
    bless( $self, $class );
    return $self;
}


#========================================================================
sub setCountLoc
{
    my $self = shift;
    $self->{countLoc} = shift;
    if (!defined $self->{countLoc})
    {
        # Default to true, if no argument given
        $self->{countLoc} = 1;
    }
}


#========================================================================
sub setInputFileName
{
    my ( $self, $fileName ) = @_;
    my @fileNameParts = split( /\./, $fileName );
    my $extension = $fileNameParts[-1];
    $extension =~ tr/A-Z/a-z/;
    if ( defined $extensionMap{$extension} )
    {
        my $language = $extensionMap{$extension};
        if ( $language eq $self->{language} )
        {
            # print "already using $language\n";
            return 1;
        }
        else
        {
            my $langMod = "Web_CAT/HFile/HFile_${language}.pm";
            # print "langMod = $langMod\n";
            foreach my $dir ( @INC )
            {
                if ( -f "$dir/$langMod" )
                {
                    require $langMod;
                    $self->{language} = $language;
                    $self->{highlightfile} =
                        eval "Web_CAT::HFile::HFile_${language}::new "
                             . "Web_CAT::HFile::HFile_${language}";
                    $self->{fileName} = $fileName;
                    return 1;
                }
            }
        }
        return 0;
    }
    else
    {
        # print "extension not recognized: $extension\n";
        return 0;
    }
}


#========================================================================
sub loadFile
{
    my $self = shift;
    my $fileName = shift;
    if ( defined $fileName )
    {
        $self->{fileName} = $fileName;
    }
    else
    {
        $fileName = $self->{fileName};
    }
    return if ( ! -f $fileName );
    open( BEAUTIFIERFILEIN, $fileName ) or return;
    my @outstr = <BEAUTIFIERFILEIN>;
    close( BEAUTIFIERFILEIN );
    if ($self->{countLoc})
    {
         $self->countLoc(\@outstr);
    }

    my $result = join( "", @outstr );
    # Strip all NULL characters, in case this is UTF-16 or something.
    $result =~ s/\x00//go;
    $result =~ s/^[\xfe\xff]+//o;
    return $result;
}


#========================================================================
sub countLoc
{
    my $self = shift;
    my $lines = shift;

    if (!defined $self->{codeMessages})
    {
        $self->{codeMessages} = {};
    }
    if (!defined $self->{codeMessages}->{$self->{fileName}})
    {
        $self->{codeMessages}->{$self->{fileName}} = {};
    }

    my $loc = 0;
    my $ncloc = 0;
    my $inComment = 0;

    # Only handles C++/Java-style comments at present.
    # Need to revise to use HFile comment info.
    foreach my $origline (@{$lines})
    {
        my $line = $origline;
        $loc++;
        if ($inComment)
        {
            if ($line =~ s,^.*?\*/,,o)
            {
                $inComment = 0;
            }
        }

        if (!$inComment)
        {
            # Eliminate all closed C-style comments on current line
            $line =~ s,/\*.*?\*/,,go;
            # There can be at most one remaining comment, which goes to
            # end of line
            if ($line =~ m,(/\*|//),o)
            {
                 if ($1 eq '/*')
                 {
                     $inComment = 1;
                 }
                 $line = $`;
            }
            if ($line !~ m/^\s*[\r\n]*$/o)
            {
                # found some code on this line
                $ncloc++;
            }
        }
    }

    $self->{codeMessages}->{$self->{fileName}}->{ncloc} = $ncloc;
    $self->{codeMessages}->{$self->{fileName}}->{loc} = $loc;
}


#========================================================================
sub extractPotentialPartners
{
    my $self = shift;
    my $lines = shift;

    if (defined $self->{cfg})
    {
        my @partnerExcludePatterns = ();
        my $partnerExcludePatterns_raw =
            $self->{cfg}->getProperty('grader.partnerExcludePatterns', '');
        if ($partnerExcludePatterns_raw ne '')
        {
            @partnerExcludePatterns =
                split(/(?<!\\),/, $partnerExcludePatterns_raw);
        }
        my $userName = $self->{cfg}->getProperty('userName', '');
        if ($userName ne '')
        {
            push(@partnerExcludePatterns, $userName);
        }
        my $potentialPartners =
            $self->{cfg}->getProperty('grader.potentialpartners', '');
        foreach my $line (@{$lines})
        {
            if ($line =~
                m/<span[^<>]*class="comment"[^<>]*>[\*\s]*\@author\s+([^<>]*)<\/span>/mo)
            {
                my $authors = $1;
                $authors =~ s/\@[a-zA-Z][a-zA-Z0-9\.]+[a-zA-Z]/ /g;
                $authors =~
                    s/your-pid [\(]?and if in lab[,]? partner[']?s pid on same line[\)]?//;
                $authors =~ s/Partner [1-9][' ]?s name [\(]?pid[\)]?//;
                $authors =~ s/[,;:\(\)\]\]\{\}=!\@#%^&\*<>\/\\\`'"\r\n]/ /mg;
                foreach my $pat (@partnerExcludePatterns)
                {
                    $authors =~ s/(?<!\S)$pat(?!\S)//g;
                }
                $authors =~ s/^\s+//;
                $authors =~ s/\s+$//;
                $authors =~ s/\s\s+/ /g;
                if ($authors ne '')
                {
                    foreach my $author (split(/\s+/, $authors))
                    {
                        if ($potentialPartners !~ m/\b\Q$author\E\b/)
                        {
                            if ($potentialPartners ne '')
                            {
                                $potentialPartners .= ' ';
                            }
                            $potentialPartners .= $author;
                        }
                    }
                }
            }
            if ($potentialPartners ne '')
            {
                $self->{cfg}->setProperty(
                    'grader.potentialpartners', $potentialPartners);
            }
        }
    }
}


#========================================================================
# Copied from clover reformatter module.
# Needs refactoring to eliminate duplication between commentBody and
# commentBody2
sub commentBody2
{
    my $self      = shift;
    my $lineNo    = shift;
    my $commentId = shift;
    my $comment   = shift;

    my $id   = 'N:' . $commentId . ':' . $lineNo;
    my $message = ($comment->{message}->null)
        ? $comment->content
        : $comment->{message}->content;
    $message = Web_CAT::Utilities::htmlEscape($message);
    my $deduction = '';
    if ($comment->{deduction}->content > 0)
    {
        $deduction = ': -' . $comment->{deduction}->content;
        if ($comment->{overLimit}->content > 0)
        {
            $deduction .=
                " <font size=\"-1\" id=\"$id\">(limit exceeded)</font>";
        }
    }
    else
    {
        if ($comment->{overLimit}->content > 0)
        {
            $deduction =
                ": 0 <font size=\"-1\" id=\"$id\">(limit exceeded)</font>";
        }
    }
    my $category = Web_CAT::Utilities::htmlEscape(
        $comment->{category}->content);
    my $source = ($comment->{message}->null)
        ? 'PMD'
        : 'Checkstyle';
    my $icon = $icons{$category};
    if (!defined($icon))
    {
        $icon = $icons{default};
    }
    my $url = $comment->{url}->content;
    if (!defined($url))
    {
        $url = '';
    }
    elsif ($url ne '')
    {
        $url = "&#160;&#160;<a id=\"$id\" href=\"$url\"><img id=\"$id\" "
            . 'src="http://web-cat.cs.vt.edu/icons/info.gif" border="0" '
            . 'width="16" height="16" target="WCHelp" alt="help"/>';
    }

    # still need to place icon

    my $result = <<EOF;
<tr id="$id"><td colspan="3" id="$id"><img src="http://web-cat.cs.vt.edu/images/blank.gif" width="1" height="2" alt="" border="0" id="$id"/></td></tr>
<tr id="$id"><td id="$id">&#160;</td><td id="$id">&#160;</td><td id="$id">
<table border="0" cellpadding="1" cellspacing="0" id="$id">
<tr id="$id"><td class="messageBox" id="$id">
<img src="$icon" width="16" height="16" alt="" id="$id"/> <b id="$id"><span id="$id:C">$category</span> \[$source\]$deduction</b>$url<br id="$id"/><i id="$id">
$message
</i></td></tr></table></td></tr>
<tr id="$id"><td colspan="3" id="$id"><img src="http://web-cat.cs.vt.edu/images/blank.gif" width="1" height="2" alt="" id="$id"/></td>
</tr>
EOF
    # print "\n\ncomment:\n--------\n", $result;
    return $result;
}


#========================================================================
sub highlightFile
{
    my $self = shift;
    my @lines = split("\n", $self->highlight_text($self->loadFile));
    my $numLines = scalar @lines;
    $self->extractPotentialPartners(\@lines);
    my @newLines = (
        qq(<TABLE cellspacing="0" cellpadding="0" class="srcView" )
            . qq(bgcolor="white" id="bigtab">\n),
        qq(<TBODY id="tab">)
        );
    my $messageCount = 0;

    for ( my $i = 0; $i < $numLines; $i++ )
    {
        my $num = $i + 1;
        my $id = "O:$num";
        my $line = $lines[$i];
        $line =~ s/"\@id\@"/"$id"/g;
        my $hl = '';
        my $Line = 'Line';
        my $starta = '';
        my $enda = '';
        my $lineClass = '';
        my $category = undef;
        my $messages = '';

        if (defined $self->{codeMessages}
            && defined $self->{codeMessages}{$self->{fileName}}
            && defined $self->{codeMessages}{$self->{fileName}}{$num})
        {
            # Handle conventional, one-off messages, plus code coverage info
            if (defined $self->{codeMessages}{$self->{fileName}}{$num}{category})
            {
                $category = $self->{codeMessages}{$self->{fileName}}{$num}{category};

                if ($categoryPriority{$category})
                {
                    $lineClass = " class=\"$category\"";
                    $messageCount++;
                    my $messageId = "N:$messageCount:$num";
                    my $commentBody = ${self}->commentBody($messageId,
                        $self->{codeMessages}{$self->{fileName}}{$num});
                    $messages .= <<EOF;
<tr id="$messageId"><td colspan="3" id="$messageId">
    <img src="http://web-cat.cs.vt.edu/images/blank.gif" width="1" height="2" border="0" id="$messageId"/>
</td></tr>
<tr id="$messageId">
    <td id="$messageId">&#160;</td>
    <td id="$messageId">&#160;</td>
    <td id="$messageId">$commentBody</td>
</tr>
<tr id="$messageId"><td colspan="3" id="$messageId">
    <img src="http://web-cat.cs.vt.edu/images/blank.gif" width="1" height="2" border="0" id="$messageId"/>
</td></tr>
EOF
                }
                else
                {
                    $starta = "<a title=\""
                         . $self->{codeMessages}{$self->{fileName}}{$num}{message}
                         . "\" id=\"$id\">";
                    $enda = "</a>";
                }
                $hl = "Hilight";
                $Line = $hl;
            }

            if (defined $self->{codeMessages}{$self->{fileName}}{$num}{violations})
            {
                foreach my $c (@{$self->{codeMessages}{$self->{fileName}}{$num}{violations}})
                {
                    if ($c->{kill}->null)
                    {
                        $messageCount++;
                        if (!defined $category
                            || !defined $categoryPriority{$category}
                            || $categoryPriority{$category} >
                               $categoryPriority{$c->{category}->content})
                        {
                            $category = $c->{category}->content;
                            $lineClass = " class=\"$category\"";
                        }
                        my $message = ($c->{message}->null)
                            ? $c->content
                            : $c->{message}->content;
#                       print "comment = ", $c->data(tree => $c), "\n";
#                       print 'group = ', $c->{group}->content, ', line = ',
#                           $c->{line}->content, ', message = ',
#                           $message, "\n";
                        $messages .=
                            $self->commentBody2($num, $messageCount, $c);
                    }
                }
            }
        }
        my $outLine = <<EOF;
<tr id="$id"$lineClass>
    <td align="right" class="lineCount$hl" id="$id">&#160;$num</td>
    <td align="right" class="lineCount$hl" id="$id">&#160;&#160;</td>
    <td class="src$Line" id="$id">
        <pre class="srcLine" id="$id">$starta&#160;$line$enda</pre>
    </td>
</tr>$messages
EOF

        push(@newLines, $outLine);
    }

    push( @newLines, "</TBODY></TABLE>\n" );
    return @newLines;
}


#========================================================================
sub commentBody
{
    my $self = shift;
    my $messageId = shift;
    my $violationRef = shift;
    my (%violation) = %{$violationRef};
    my $category = $violation{category};
    my $deduction = "";
    if ($violation{deduction})
    {
        $deduction = ": -" . $violation{deduction};
    }
    my $message = $violation{message};
    my $icon = $icons{$category};
    if ( !defined( $icon ) )
    {
        $icon = $icons{default};
    }

    return <<EOF;
<table border="0" cellpadding="1" cellspacing="0" id="$messageId"><tbody>
    <tr id="$messageId"><td class="messageBox" id="$messageId">
        <img src="$icon" width="16" height="16" id="$messageId"/>
        <b id="$messageId"><span id="$messageId">$category</span>$deduction</b>
        <br id="$messageId"/>
        <i id="$messageId">$message</i>
    </td></tr>
</tbody></table>
EOF
}


#========================================================================
sub generateHighlightOutputFile
{
    my $self = shift;
    my $destPrefix = shift;
    my $fileName = shift;
    if ( defined $fileName )
    {
        $self->{fileName} = $fileName;
    }
    else
    {
        $fileName = $self->{fileName};
    }
    # print "  checking for $fileName\n";
    return if ( ! -f $fileName );
    # print "   found\n";

    # print "$fileName is file\n";

    if ( $self->setInputFileName( $fileName ) )
    {
        my $outFileName = $fileName . ".html";
        if ( defined $destPrefix )
        {
            $outFileName = "$destPrefix/$outFileName";
        }
        # print "    target => $outFileName\n";
        if ( $outFileName =~ m,/,o )
        {
            my @dirPath = split( "/", $outFileName );
            pop( @dirPath );
            mkpath( join( "/", @dirPath ) );
        }
        # print "    attempting to open output file\n";
        open( BEAUTIFIERFILEOUT, ">:utf8", $outFileName ) or return;
        my @lines = $self->highlightFile;
        print BEAUTIFIERFILEOUT @lines;
        close( BEAUTIFIERFILEOUT );
        return $outFileName;
    }
    else
    {
        return;
    }
}


#========================================================================
sub beautify
{
    my $self           = shift;
    my $fileName       = shift;
    my $outBase        = shift;
    my $outPrefix      = shift;
    my $numCodeMarkups = shift;
    my $skipExtensions = shift;
    my $cfg            = shift;
    # print "attempting to beautify $fileName\n";

    if ( defined $skipExtensions )
    {
        foreach my $ext ( @{$skipExtensions} )
        {
            if ( $fileName =~ m,\Q$ext\E$, )
            {
                return;
            }
        }
    }
    if ( -d $fileName )
    {
        # print "$fileName is directory\n";

        foreach my $f ( <$fileName/*> )
        {
            $self->beautify( $f, $outBase, $outPrefix, $numCodeMarkups,
                             $skipExtensions, $cfg );
        }
    }
    else
    {
        my $thisMarkup = 0;
        my $trimmedName = $fileName;
        if (defined $self->{sourcePrefix})
        {
            $trimmedName =~ s/^\Q$self->{sourcePrefix}\E//;
        }

        if (defined $self->{codeMarkupIds})
        {
            # print "searching for trimmed name: '$trimmedName'\n";
            if (defined $self->{codeMarkupIds}->{$trimmedName})
            {
                $thisMarkup = $self->{codeMarkupIds}->{$trimmedName};
            }
            else
            {
                my $mungedFileName = lcfirst($trimmedName);
                if (defined $self->{codeMarkupIds}->{$mungedFileName})
                {
                    $thisMarkup = $self->{codeMarkupIds}->{$mungedFileName};
                    $trimmedName = $mungedFileName;
                }
            }

            if (!$thisMarkup && !defined $self->{sourcePrefix})
            {
                # print "trying to find source prefix\n";
                $self->{sourcePrefix} = '';
                while (!$thisMarkup && $trimmedName =~ m,^[^/]+/,o)
                {
                    $self->{sourcePrefix} .= $&;
                    $trimmedName = $';
                    # print "    checking source prefix = '",
                    #    $self->{sourcePrefix}, "', trimmed name = '",
                    #    $trimmedName = $', "'\n";
                    if (defined $self->{codeMarkupIds}->{$trimmedName})
                    {
                        $thisMarkup = $self->{codeMarkupIds}->{$trimmedName};
                    }
                    else
                    {
                        my $mungedFileName = lcfirst($trimmedName);
                        if (defined $self->{codeMarkupIds}->{$mungedFileName})
                        {
                            $thisMarkup =
                                $self->{codeMarkupIds}->{$mungedFileName};
                            $trimmedName = $mungedFileName;
                        }
                    }
                }
                if (!$thisMarkup)
                {
                    delete $self->{sourcePrefix};
                }
            }
        }
        if ($thisMarkup
            && $trimmedName ne $fileName
            && defined $self->{codeMarkupIds}
            && defined $self->{codeMarkupIds}->{$trimmedName})
        {
            $self->{codeMarkupIds}->{$fileName} =
                $self->{codeMarkupIds}->{$trimmedName};
            if (defined $self->{codeMessages}
                && defined $self->{codeMessages}->{$trimmedName})
            {
                foreach my $key (keys %{$self->{codeMessages}->{$trimmedName}})
                {
                    $self->{codeMessages}->{$fileName}{$key} =
                        $self->{codeMessages}->{$trimmedName}{$key};
                }
            }
        }

        if (defined $cfg)
        {
            $self->{cfg} = $cfg;
        }
        my $outfile = $self->generateHighlightOutputFile(
            "$outBase/$outPrefix", $fileName);
        if (defined $self->{cfg})
        {
            delete $self->{cfg};
        }

        if ( defined $outfile )
        {
            if (!$thisMarkup)
            {
                $$numCodeMarkups++;
                $thisMarkup = $$numCodeMarkups;
            }
            $cfg->setProperty("codeMarkup${thisMarkup}.sourceFileName",
                              $fileName);
            $outfile =~ s,^$outBase/,,o;
            $cfg->setProperty("codeMarkup${thisMarkup}.markupFileName",
                              $outfile);
            if (defined $self->{codeMessages}
                && defined $self->{codeMessages}->{$fileName})
            {
                if (defined $self->{codeMessages}->{$fileName}->{loc})
                {
                    $cfg->setProperty("codeMarkup${thisMarkup}.loc",
                        $self->{codeMessages}->{$fileName}->{loc});
                }
                if (defined $self->{codeMessages}->{$fileName}->{ncloc})
                {
                    $cfg->setProperty("codeMarkup${thisMarkup}.ncloc",
                        $self->{codeMessages}->{$fileName}->{ncloc});
                }
            }
        }
    }
}


#========================================================================
sub beautifyCwd
{
    my $self           = shift;
    my $cfg            = shift;
    my $skipExtensions = shift;
    my $codeMarkupIds  = shift;
    my $codeMessages   = shift;
    my $outBase        = $cfg->getProperty( 'resultDir' );
    my $outPrefix      = 'html';
    my $numCodeMarkups = $cfg->getProperty( 'numCodeMarkups', 0 );

    if ( defined $codeMarkupIds )
    {
        $self->{codeMarkupIds} = $codeMarkupIds;
    }
    elsif ( defined $self->{codeMarkupIds} )
    {
        delete $self->{codeMarkupIds};
    }

    if ( defined $codeMessages )
    {
        $self->{codeMessages} = $codeMessages;
    }
    elsif ( defined $self->{codeMessages} )
    {
        delete $self->{codeMessages};
    }

    if (defined $self->{sourcePrefix})
    {
        delete $self->{sourcePrefix};
    }

    foreach my $f ( <*> )
    {
        $self->beautify( $f, $outBase, $outPrefix, \$numCodeMarkups,
                         $skipExtensions, $cfg );
    }
    $cfg->setProperty( 'numCodeMarkups', $numCodeMarkups );
}


# ---------------------------------------------------------------------------
1;
# ---------------------------------------------------------------------------
