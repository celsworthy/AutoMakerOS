package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.OrphanObjectDelineationNode;
import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.Rule;

/**
 *
 * @author Ian
 */
//@BuildParseTree
public class CuraGCodeParser extends GCodeParser
{
    @Override
    public Rule Layer()
    {
        return Sequence(Sequence(";LAYER:", IntegerNumber(),
                (Action) (Context context1) ->
                {
                    thisLayer.setLayerNumber(Integer.valueOf(context1.getMatch()));
                    thisLayer.setGCodeLineNumber(++currentLineNumber);
                    return true;
                },
                Newline()
        ),
                ZeroOrMore(
                        FirstOf(
                                ObjectSection(),
                                OrphanObjectSection(),
                                ChildDirective()
                        ),
                        (Action) (Context context1) ->
                        {
                            if (!context1.getValueStack().isEmpty())
                            {
                                GCodeEventNode node = (GCodeEventNode) context1.getValueStack().pop();
                                thisLayer.addChildAtEnd(node);
                            }
                            return true;
                        }
                ),
                (Action) (Context context1) ->
                {
                    thisLayer.setLayerHeight_mm(currentLayerHeight);
                    return true;
                }
        );
    }
    
    // No preceding T command - can happen at the start of a file or start of a layer if the tool use is continued from the previous
    @Override
    Rule OrphanObjectSection()
    {
        OrphanObjectSectionActionClass orphanObjectSectionAction = new OrphanObjectSectionActionClass();

        return Sequence(
                // Orphan - make this part of the current object
                IsASection(),
                orphanObjectSectionAction,
                OneOrMore(
                        Sequence(
                                FirstOf(
                                        TravelDirective(),
                                        AnySection()
                                ),
                                new Action()
                                {
                                    @Override
                                    public boolean run(Context context)
                                    {
                                        orphanObjectSectionAction.getNode().addChildAtEnd((GCodeEventNode) context.getValueStack().pop());
                                        return true;
                                    }
                                }
                        )
                ),
                new Action()
                {
                    @Override
                    public boolean run(Context context
                    )
                    {
                        OrphanObjectDelineationNode node = orphanObjectSectionAction.getNode();
                        node.setPotentialObjectNumber(currentObject);
                        node.setGCodeLineNumber(++currentLineNumber);
                        context.getValueStack().push(node);
                        return true;
                    }
                }
        );
    }
}
