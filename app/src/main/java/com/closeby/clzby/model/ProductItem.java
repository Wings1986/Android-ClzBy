package com.closeby.clzby.model;

import java.io.Serializable;

/**
 * Created by iGold on 6/30/15.
 */

public class ProductItem implements Serializable {

    public String ID;
    public String ProductName;
    public String CategoryIds;
    public int OrigionalPrice;
    public String ProductDescription;
    public String ProductPhoto;

    public String DiscountedTagLing;
    public int QuantityRemaining;
    public int SpecialPrice;
    public int DecayDuration;
}