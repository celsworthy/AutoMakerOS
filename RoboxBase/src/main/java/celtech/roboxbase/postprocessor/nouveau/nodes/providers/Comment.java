package celtech.roboxbase.postprocessor.nouveau.nodes.providers;

/**
 *
 * @author Ian
 */
public final class Comment
{
    //This comment field is only used if a subclass has an inline comment

    private String comment = "";

    public Comment()
    {
    }

    public Comment(String comment)
    {
        this.comment = comment;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public void appendComment(String comment)
    {
        this.comment += " " + comment;
    }

    public String renderComments()
    {
        StringBuilder stringToReturn = new StringBuilder();
        if (getComment().length() > 0)
        {
            stringToReturn.append(";");
            stringToReturn.append(getComment());
        }

        return stringToReturn.toString();
    }
    
    public Comment clone()
    {
        Comment newNode = new Comment();
        newNode.comment = this.comment;
        
        return newNode;
    }
}
