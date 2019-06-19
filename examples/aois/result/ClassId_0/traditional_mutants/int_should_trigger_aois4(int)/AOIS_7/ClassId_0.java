// This is a mutant program.
// Author : ysma

import java.util.*;


/** @ContextInfo(
MutationOperatorGroup=AOIS
Before=field
After=field++
MutatedLine=-1
AstContext=null
)*/
public class ClassId_0
{

    int field = 5;

    public ClassId_0()
    {
    }

    public  int should_trigger_aois3( int param )
    {
        param *= field;
        return param;
    }

    public  int should_trigger_aois4( int param )
    {
        param *= field++;
        return param;
    }

    public  int should_trigger_aois3a( int param )
    {
        param = param * field;
        return param;
    }

    public  int should_trigger_aois4a( int param )
    {
        param = param * field;
        return param;
    }

}
