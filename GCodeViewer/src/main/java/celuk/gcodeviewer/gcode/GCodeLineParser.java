package celuk.gcodeviewer.gcode;

import java.util.Map;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Rule;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

/**
 *
 * @author Tony Aldhous
 */
//@BuildParseTree
public class GCodeLineParser extends BaseParser<GCodeLine>
{
    private final Stenographer steno = StenographerFactory.getStenographer(GCodeLineParser.class.getName());

    private final GCodeLine line = new GCodeLine();
    private Map<String, Double> settingsMap = null;
    
    public GCodeLine getLine()
    {
        return line;
    }

    public void resetLine()
    {
        line.reset();
    }

    public Map<String, Double> getSettingsMap()
    {
        return this.settingsMap;
    }

    public void setSettingsMap(Map<String, Double> settingsMap)
    {
        this.settingsMap = settingsMap;
    }

    public Rule Line()
    {
        return FirstOf(
            Command(),
            TypeComment(),
            LayerComment(),
            SettingsComment(),
            Comment(),
            EqualsComment()
        );
    }
    
    // A M or G command e.g. G0 F12000 X88.302 Y42.421 Z1.020
    @SuppressSubnodes
    Rule Command()
    {
        Var<Character> commandLetterValue = new Var<>();
        Var<Integer> commandNumberValue = new Var<>();
        Var<Character> valKey = new Var<>();
        Var<Double> valValue = new Var<>();

        return Sequence(
                    FirstOf('G', 'M', 'T'),
                    commandLetterValue.set(match().charAt(0)),
                    ZeroOrMore(' '),
                    OneOrMore(Digit()),
                    commandNumberValue.set(Integer.valueOf(match())),
                    ZeroOrMore(' '),
                    ZeroOrMore(
                        Sequence(
                            CharRange('A', 'Z'),
                            valKey.set(match().charAt(0)),
                            ZeroOrMore(' '),
                            Optional(
                                Sequence(
                                    FloatingPointNumber(),
                                    valValue.set(Double.valueOf(match())),
                                    ZeroOrMore(' ')
                                )
                            ),
                            new Action()
                            {
                                @Override
                                public boolean run(Context context)
                                {
                                    if (valValue.isSet())
                                        line.setValue(valKey.get(), valValue.get());
                                    else
                                        line.setValue(valKey.get(), Double.NaN);
                                    return true;
                                }
                            }
                        )
                    ),
                    Optional(
                        FirstOf(PartialBComment(),
                                Comment())
                    ),
                    new Action()
                    {
                        @Override
                        public boolean run(Context context)
                        {
                            line.commandLetter = commandLetterValue.get();
                            line.commandNumber = commandNumberValue.get();
                            return true;
                        }
                    }
                );
    }
    
    // Comment specifying a type.
    // ;TYPE:FILL\n
    @SuppressSubnodes
    Rule TypeComment()
    {
        return Sequence(
                    ZeroOrMore(' '),
                    ZeroOrMore(' '),
                    ';',
                    ZeroOrMore(' '),
                    IgnoreCase("TYPE"),
                    ZeroOrMore(' '),
                    ':',
                    ZeroOrMore(' '),
                    OneOrMore(
                        FirstOf(
                            CharRange('a', 'z'),
                            CharRange('A', 'Z'),
                            '-'
                        )
                    ),
                    new Action()
                    {
                        @Override
                        public boolean run(Context context)
                        {
                            line.type = match().trim();
                            return true;
                        }
                    }
                );
    }

    // Comment specifying a layer.
    // ;LAYER:34 height:1.47
    @SuppressSubnodes
    Rule LayerComment()
    {
        Var<Integer> layerValue = new Var<>();
        Var<Double> heightValue = new Var<>();

        return Sequence(
                    ZeroOrMore(' '),
                    ';',
                    ZeroOrMore(' '),
                    IgnoreCase("LAYER"),
                    ZeroOrMore(' '),
                    Optional(
                        Sequence(
                            ':',
                            ZeroOrMore(' ')
                        )
                    ),
                    Sequence(
                        Optional(
                            Sequence(    
                                FirstOf(
                                    Ch('+'),
                                    Ch('-')
                                ),
                                ZeroOrMore(' ')
                            )       
                        ),
                        OneOrMore(Digit())),
                    layerValue.set(Integer.valueOf(match())),
                    
                    Optional(
                        Sequence(
                            ZeroOrMore(' '),
                            IgnoreCase("HEIGHT"),
                            ZeroOrMore(' '),
                            Optional(
                                Sequence(
                                    ':',
                                    ZeroOrMore(' ')
                                )
                            ),
                            PositiveFloatingPointNumber(),
                            heightValue.set(Double.valueOf(match()))
                        )
                    ),
                    ZeroOrMore(ANY),
                    new Action()
                    {
                        @Override
                        public boolean run(Context context)
                        {
                            line.layerNumber = layerValue.get();
                            if (heightValue.isSet())
                                line.layerHeight = heightValue.get();
                            line.comment = match().trim();
                            return true;
                        }
                    }                    
                );
    }
    
    // Comment specifying a partial open, with the original B value.
    // ;PARTIAL OPEN B0.102
    @SuppressSubnodes
    Rule PartialBComment()
    {
        Var<Double> idealBValue = new Var<>();
        
        return Sequence(
                    ZeroOrMore(' '),
                    ';',
                    ZeroOrMore(' '),
                    IgnoreCase("PARTIAL"),
                    OneOrMore(' '),
                    IgnoreCase("OPEN"),
                    ZeroOrMore(' '),
                    IgnoreCase("B"),
                    ZeroOrMore(' '),
                    PositiveFloatingPointNumber(),
                    idealBValue.set(Double.valueOf(match())),
                    new Action()
                    {
                        @Override
                        public boolean run(Context context)
                        {
                            line.setValue('b', idealBValue.get());
                            line.comment = match().trim();
                            return true;
                        }
                    }
                );
    }

    // Comment specifying a setting.
    // ;# infillLayerThickness = 0.3
    @SuppressSubnodes
    Rule SettingsComment()
    {
        Var<String> settingId = new Var<>();
        Var<Double> settingValue = new Var<>();

        return Sequence(
                    ZeroOrMore(' '),
                    ";#",
                    ZeroOrMore(' '),
                    Identifier(),
                    settingId.set(match()),
                    ZeroOrMore(' '),
                    '=',
                    ZeroOrMore(' '),
                    PositiveFloatingPointNumber(),
                    settingValue.set(Double.valueOf(match())),
                    new Action()
                    {
                        @Override
                        public boolean run(Context context)
                        {
                            line.comment = match().trim();
                            settingsMap.put(settingId.get(), settingValue.get());
                            return true;
                        }
                    }                    
                );
    }
    
    // Comment element.
    // ;Blah blah blah\n
    @SuppressSubnodes
    Rule Comment()
    {
        return Sequence(
                ZeroOrMore(' '),
                ';',
                ZeroOrMore(ANY),
                new Action()
                {
                    @Override
                    public boolean run(Context context)
                    {
                        line.comment = match().trim();
                        return true;
                    }
                }
        );
    }
    
    // Comment element.
    // =Blah blah blah\n
    @SuppressSubnodes
    Rule EqualsComment()
    {
        return Sequence(
                ZeroOrMore(' '),
                FirstOf(
                    Ch('='),
                    Ch('<')
                ),
                ZeroOrMore(ANY),
                new Action()
                {
                    @Override
                    public boolean run(Context context)
                    {
                        line.comment = match().trim();
                        return true;
                    }
                }
        );
    }

    @SuppressSubnodes
    Rule Digit()
    {
        return CharRange('0', '9');
    }

    @SuppressSubnodes
    Rule Identifier()
    {
        return Sequence(FirstOf(
                            CharRange('a', 'z'),
                            CharRange('A', 'Z')),
                        OneOrMore(
                            FirstOf(
                                CharRange('a', 'z'),
                                CharRange('A', 'Z'),
                                CharRange('0', '9'),
                                '-',
                                '_')));
    }

    @SuppressSubnodes
    Rule FloatingPointNumber()
    {
        return Sequence(
                    Optional(
                        Sequence(
                            FirstOf(
                                Ch('+'),
                                Ch('-')
                            ),
                            ZeroOrMore(' ')
                        )
                    ),
                    UnsignedFloatingPointNumber()
                );
    }
    
    @SuppressSubnodes
    Rule UnsignedFloatingPointNumber()
    {
        //unsigned double e.g. 123, 1.23 or .123
        return FirstOf(
                    Sequence(
                        OneOrMore(Digit()),
                        Optional(
                            Sequence(
                                Ch('.'),
                                OneOrMore(Digit())
                            )
                        )
                    ),
                    Sequence(
                        Ch('.'),
                        OneOrMore(Digit())
                    )
                );
    }

    @SuppressSubnodes
    Rule PositiveFloatingPointNumber()
    {
        //Positive double e.g. 1.23
        return Sequence(
                Optional(
                    Sequence(
                        Ch('+'),
                        ZeroOrMore(' ')
                    )
                ),
                UnsignedFloatingPointNumber());
    }

    @SuppressSubnodes
    Rule NegativeFloatingPointNumber()
    {
        //Negative double e.g. -1.23
        return Sequence(
                Ch('-'),
                ZeroOrMore(' '),
                UnsignedFloatingPointNumber());
    }
}
