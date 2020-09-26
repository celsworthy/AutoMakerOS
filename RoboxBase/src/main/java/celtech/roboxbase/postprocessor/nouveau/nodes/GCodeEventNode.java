package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.IteratorWithOrigin;
import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.IteratorWithStartPoint;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Comment;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public abstract class GCodeEventNode
{

    private final Comment comment = new Comment();
    private Optional<Integer> gCodeLineNumber = Optional.empty();
    private Optional<GCodeEventNode> parent = Optional.empty();
    protected final LinkedList<GCodeEventNode> children = new LinkedList<>();
    private Optional<Double> finishTimeFromStartOfPrint_secs = Optional.empty();

    public GCodeEventNode()
    {
    }

    public boolean isLeaf()
    {
        return children.isEmpty();
    }

    public boolean hasParent()
    {
        return parent.isPresent();
    }

    public IteratorWithOrigin<GCodeEventNode> childrenAndMeBackwardsIterator()
    {
        IteratorWithOrigin<GCodeEventNode> it = new IteratorWithOrigin<GCodeEventNode>()
        {
            private GCodeEventNode originNode;
            private int currentIndex = children.size() - 1;
            private Iterator<GCodeEventNode> childIterator = null;

            @Override
            public void setOriginNode(GCodeEventNode originNode)
            {
                this.originNode = originNode;
            }

            @Override
            public boolean hasNext()
            {
                return currentIndex >= -1;
            }

            @Override
            public GCodeEventNode next()
            {
                if (currentIndex >= 0)
                {
                    if (childIterator != null
                            && childIterator.hasNext())
                    {
                        return childIterator.next();
                    } else if (childIterator != null)
                    {
                        childIterator = null;
                        GCodeEventNode child = children.get(currentIndex);
                        currentIndex--;
                        return child;
                    } else
                    {
                        GCodeEventNode child = children.get(currentIndex);
                        if (child.isLeaf())
                        {
                            currentIndex--;
                            return child;
                        } else
                        {
                            childIterator = child.children.descendingIterator();
                            return childIterator.next();
                        }
                    }

                } else
                {
                    currentIndex--;
                    return originNode;
                }
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };

        it.setOriginNode(this);
        return it;
    }

    public IteratorWithOrigin<GCodeEventNode> meAndSiblingsBackwardsIterator()
    {
        IteratorWithOrigin<GCodeEventNode> it = new IteratorWithOrigin<GCodeEventNode>()
        {
            private GCodeEventNode originNode;
            private int currentIndex;

            @Override
            public void setOriginNode(GCodeEventNode originNode)
            {
                this.originNode = originNode;
                currentIndex = parent.get().children.indexOf(originNode);
            }

            @Override
            public boolean hasNext()
            {
                return currentIndex >= 0;
            }

            @Override
            public GCodeEventNode next()
            {
                return parent.get().children.get(currentIndex--);
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };

        it.setOriginNode(this);
        return it;
    }

    /**
     *
     * @return
     */
    public IteratorWithOrigin<GCodeEventNode> siblingsBackwardsIterator()
    {
        IteratorWithOrigin<GCodeEventNode> it = new IteratorWithOrigin<GCodeEventNode>()
        {
            private GCodeEventNode originNode;
            private int currentIndex;

            @Override
            public void setOriginNode(GCodeEventNode originNode)
            {
                this.originNode = originNode;
                currentIndex = parent.get().children.indexOf(originNode) - 1;
            }

            @Override
            public boolean hasNext()
            {
                return currentIndex >= 0;
            }

            @Override
            public GCodeEventNode next()
            {
                return parent.get().children.get(currentIndex--);
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };

        it.setOriginNode(this);
        return it;
    }

    public IteratorWithOrigin<GCodeEventNode> siblingsIterator()
    {
        IteratorWithOrigin<GCodeEventNode> it = new IteratorWithOrigin<GCodeEventNode>()
        {
            private GCodeEventNode originNode;
            private int currentIndex;

            @Override
            public void setOriginNode(GCodeEventNode originNode)
            {
                this.originNode = originNode;
                currentIndex = parent.get().children.indexOf(originNode) + 1;
            }

            @Override
            public boolean hasNext()
            {
                return currentIndex >= 0 && currentIndex < parent.get().children.size();
            }

            @Override
            public GCodeEventNode next()
            {
                return parent.get().children.get(currentIndex++);
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };

        it.setOriginNode(this);
        return it;
    }

    public IteratorWithOrigin<GCodeEventNode> meAndSiblingsIterator()
    {
        IteratorWithOrigin<GCodeEventNode> it = new IteratorWithOrigin<GCodeEventNode>()
        {
            private GCodeEventNode originNode;
            private int currentIndex;

            @Override
            public void setOriginNode(GCodeEventNode originNode)
            {
                this.originNode = originNode;
                currentIndex = parent.get().children.indexOf(originNode);
            }

            @Override
            public boolean hasNext()
            {
                return currentIndex >= 0 && currentIndex < parent.get().children.size();
            }

            @Override
            public GCodeEventNode next()
            {
                return parent.get().children.get(currentIndex++);
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };

        it.setOriginNode(this);
        return it;
    }

    public IteratorWithStartPoint<GCodeEventNode> treeSpanningIterator(List<GCodeEventNode> startNodeHierarchy)
    {
        IteratorWithStartPoint<GCodeEventNode> it = new IteratorWithStartPoint<GCodeEventNode>(startNodeHierarchy)
        {
            private boolean needToConsumeInitialValue;
            private int childIndex;
            private Iterator<GCodeEventNode> childIterator;

            @Override
            public boolean hasNext()
            {
                return (childIndex < children.size() && !needToConsumeInitialValue)
                        || (childIndex < children.size() - 1 && needToConsumeInitialValue)
                        || (childIterator != null && childIterator.hasNext());
            }

            @Override
            public GCodeEventNode next()
            {
                GCodeEventNode childToReturn = null;

                if (childIterator != null
                        && childIterator.hasNext())
                {
                    childToReturn = childIterator.next();
                } else
                {
                    childIterator = null;

                    if (needToConsumeInitialValue)
                    {
                        needToConsumeInitialValue = false;
                        childIndex++;
                    }

                    GCodeEventNode child = children.get(childIndex++);
                    if (!child.isLeaf())
                    {
                        childIterator = child.treeSpanningIterator(startNodeHierarchy);
                    }
                    childToReturn = child;
                }

                return childToReturn;
            }

            @Override
            public void initialiseWithList(List<GCodeEventNode> startNodeHierarchy)
            {
                if (startNodeHierarchy != null
                        && !startNodeHierarchy.isEmpty())
                {
                    childIndex = children.indexOf(startNodeHierarchy.get(0));
                    startNodeHierarchy.remove(0);

                    needToConsumeInitialValue = true;

                    childIterator = children.get(childIndex).treeSpanningIterator(startNodeHierarchy);
                } else
                {
                    childIndex = 0;
                    childIterator = null;
                    needToConsumeInitialValue = false;
                }
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };

        return it;
    }

    public IteratorWithOrigin<GCodeEventNode> treeSpanningBackwardsIterator()
    {
        IteratorWithOrigin<GCodeEventNode> it = new IteratorWithOrigin<GCodeEventNode>()
        {
            private GCodeEventNode originNode;
            private int currentIndex;
            private IteratorWithOrigin<GCodeEventNode> parentIterator = null;
            private Iterator<GCodeEventNode> childIterator = null;

            @Override
            public void setOriginNode(GCodeEventNode originNode)
            {
                this.originNode = originNode;
                if (parent.isPresent())
                {
                    currentIndex = parent.get().children.indexOf(originNode) - 1;
                } else
                {
                    currentIndex = -1;
                }
            }

            @Override
            public boolean hasNext()
            {
                return currentIndex >= 0
                        || ((parentIterator != null && parentIterator.hasNext())
                        || (childIterator != null && childIterator.hasNext())
                        || (originNode != null && originNode.hasParent()));
            }

            @Override
            public GCodeEventNode next()
            {
                if (childIterator != null
                        && childIterator.hasNext())
                {
                    return childIterator.next();
                } else
                {
                    childIterator = null;
                }

                if (parentIterator != null
                        && parentIterator.hasNext())
                {
                    return parentIterator.next();
//                    if (sibling.isLeaf())
//                    {
//                        return sibling;
//                    } else
//                    {
//                        childIterator = sibling.childrenAndMeBackwardsIterator();
//                        return childIterator.next();
//                    }
                } else
                {
                    parentIterator = null;
                }

                if (currentIndex >= 0)
                {
                    GCodeEventNode child = parent.get().children.get(currentIndex--);
                    if (child.isLeaf())
                    {
                        return child;
                    } else
                    {
                        childIterator = child.childrenAndMeBackwardsIterator();
                        return childIterator.next();
                    }
                } else
                {
                    //Look upwards from the origin node
                    GCodeEventNode parentNode = originNode.parent.get();
                    parentIterator = parentNode.treeSpanningBackwardsIterator();
                    parentIterator.setOriginNode(parentNode);
                    originNode = null;
                    return parentNode;
                }
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };

        it.setOriginNode(this);
        return it;
    }

    public IteratorWithOrigin<GCodeEventNode> treeSpanningBackwardsAndMeIterator()
    {
        IteratorWithOrigin<GCodeEventNode> it = new IteratorWithOrigin<GCodeEventNode>()
        {
            private GCodeEventNode originNode;
            private int currentIndex;
            private IteratorWithOrigin<GCodeEventNode> parentIterator = null;
            private Iterator<GCodeEventNode> childIterator = null;
            private boolean originNodeConsumed = false;

            @Override
            public void setOriginNode(GCodeEventNode originNode)
            {
                this.originNode = originNode;
                if (parent.isPresent())
                {
                    currentIndex = parent.get().children.indexOf(originNode) - 1;
                } else
                {
                    currentIndex = -1;
                }
            }

            @Override
            public boolean hasNext()
            {
                return currentIndex >= 0
                        || ((parentIterator != null && parentIterator.hasNext())
                        || (childIterator != null && childIterator.hasNext())
                        || (originNode != null && originNode.hasParent()));
            }

            @Override
            public GCodeEventNode next()
            {
                if (!originNodeConsumed)
                {
                    originNodeConsumed = true;
                    return originNode;
                }

                if (childIterator != null
                        && childIterator.hasNext())
                {
                    return childIterator.next();
                } else
                {
                    childIterator = null;
                }

                if (parentIterator != null
                        && parentIterator.hasNext())
                {
                    return parentIterator.next();
//                    if (sibling.isLeaf())
//                    {
//                        return sibling;
//                    } else
//                    {
//                        childIterator = sibling.childrenAndMeBackwardsIterator();
//                        return childIterator.next();
//                    }
                } else
                {
                    parentIterator = null;
                }

                if (currentIndex >= 0)
                {
                    GCodeEventNode child = parent.get().children.get(currentIndex--);
                    if (child.isLeaf())
                    {
                        return child;
                    } else
                    {
                        childIterator = child.childrenAndMeBackwardsIterator();
                        return childIterator.next();
                    }
                } else
                {
                    //Look upwards from the origin node
                    GCodeEventNode parentNode = originNode.parent.get();
                    parentIterator = parentNode.treeSpanningBackwardsIterator();
                    parentIterator.setOriginNode(parentNode);
                    originNode = null;
                    return parentNode;
                }
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };

        it.setOriginNode(this);
        return it;
    }

    public Iterator<GCodeEventNode> childIterator()
    {
        return children.listIterator();
    }

    public Iterator<GCodeEventNode> childBackwardsIterator()
    {
        return children.descendingIterator();
    }

    public LinkedList<GCodeEventNode> getChildren()
    {
        return children;
    }

    public void addSiblingBefore(GCodeEventNode newNode)
    {
        if (parent.isPresent())
        {
            GCodeEventNode parentNode = parent.get();
            int myIndex = parentNode.children.indexOf(this);
            parentNode.children.add(myIndex, newNode);
            newNode.parent = Optional.of(parentNode);
        }
    }

    public void addSiblingAfter(GCodeEventNode newNode)
    {
        if (parent.isPresent())
        {
            GCodeEventNode parentNode = parent.get();
            int myIndex = parentNode.children.indexOf(this);
            parentNode.children.add(myIndex + 1, newNode);
            newNode.parent = Optional.of(parentNode);
        }
    }

    public void removeFromParent()
    {
        if (parent.isPresent())
        {
            parent.get().children.remove(this);
            parent = Optional.empty();
        }
    }
//
//    public void removeFromParentAndFixup()
//    {
//        if (parent.isPresent())
//        {
//            parent.get().removeChild(this);
//            parent = Optional.empty();
//
//            if (priorSibling.isPresent()
//                    && nextSibling.isPresent())
//            {
//                priorSibling.get().setNext(nextSibling.get());
//                nextSibling.get().setPrior(priorSibling.get());
//            } else if (priorSibling.isPresent())
//            {
//                priorSibling.get().setNext(null);
//            } else if (nextSibling.isPresent())
//            {
//                nextSibling.get().setPrior(null);
//            }
//        }
//    }
//    /**
//     * Adds a child node at the end of the list of this node's children
//     *
//     * @param node
//     */
//    public void addChildAtEnd(GCodeEventNode node)
//    {
//        addChild(getChildren().size(), node);
//    }

    public Optional<GCodeEventNode> getParent()
    {
        return parent;
    }

    /**
     *
     * @return
     */
    public Optional<GCodeEventNode> getSiblingBefore()
    {
        Optional<GCodeEventNode> returnValue = Optional.empty();

        if (parent.isPresent())
        {
            GCodeEventNode parentNode = parent.get();
            int myIndex = parentNode.children.indexOf(this);
            if (myIndex > 0)
            {
                return Optional.of(parentNode.children.get(myIndex - 1));
            }
        }

        return returnValue;
    }

    /**
     *
     * @return
     */
    public Optional<GCodeEventNode> getSiblingAfter()
    {
        Optional<GCodeEventNode> returnValue = Optional.empty();

        if (parent.isPresent())
        {
            GCodeEventNode parentNode = parent.get();
            int myIndex = parentNode.children.indexOf(this);
            if (myIndex < parentNode.children.size() - 1)
            {
                return Optional.of(parentNode.children.get(myIndex + 1));
            }
        }

        return returnValue;
    }

    public GCodeEventNode getAbsolutelyTheLastEvent()
    {
        if (!isLeaf())
        {
            return children.getLast().getAbsolutelyTheLastEvent();
        } else
        {
            return this;
        }
    }

    /**
     *
     * @param newNode
     */
    public void addChildAtEnd(GCodeEventNode newNode)
    {
        children.addLast(newNode);
        newNode.parent = Optional.of(this);
    }

    /**
     *
     * @param newNode
     */
    public void addChildAtStart(GCodeEventNode newNode)
    {
        children.addFirst(newNode);
        newNode.parent = Optional.of(this);
    }

    public String getCommentText()
    {
//        if (getFinishTimeFromStartOfPrint_secs().isPresent())
//        {
//            return comment.renderComments() + " ; Time " + getFinishTimeFromStartOfPrint_secs().get();
//        } else
//        {
            return comment.renderComments();
//        }
    }

    public String getRawCommentText()
    {
        return comment.getComment();
    }

    public void setCommentText(String commentText)
    {
        comment.setComment(commentText);
    }

    public void appendCommentText(String commentText)
    {
        comment.setComment(comment.getComment() + " " + commentText);
    }

    public Optional<Integer> getGCodeLineNumber()
    {
        return gCodeLineNumber;
    }

    public void setGCodeLineNumber(int gCodeLineNumber)
    {
        this.gCodeLineNumber = Optional.of(gCodeLineNumber);
    }

    public Optional<Double> getFinishTimeFromStartOfPrint_secs()
    {
        return finishTimeFromStartOfPrint_secs;
    }

    public void setFinishTimeFromStartOfPrint_secs(double value)
    {
        finishTimeFromStartOfPrint_secs = Optional.of(value);
    }
    
    public void dumpTree() 
    {
        dumpTreeImpl(0, "");
    }

    private void dumpTreeImpl(int level, String indent) 
    {
        Iterator<GCodeEventNode> cIterator = childIterator();
        String s = indent + this.getClass().getSimpleName();
        if (this instanceof Renderable) {
            s += ": \"" + ((Renderable)this).renderForOutput().trim() + "\""; 
        }
        System.out.println(s);

        int nextLevel = level + 1;
        String nextIdent = indent + "  ";
        while (cIterator.hasNext())
        {
            GCodeEventNode node = cIterator.next();
            node.dumpTreeImpl(nextLevel, nextIdent);
        }
    }

}
