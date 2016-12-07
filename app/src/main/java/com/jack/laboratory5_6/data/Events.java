package com.jack.laboratory5_6.data;

import java.util.ArrayList;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
@XStreamAlias("Events")
public class Events{
    @XStreamImplicit(itemFieldName = "Data")
    public ArrayList<Data> data;
    public ArrayList<Data> getData() {
        return data;
    }
}
