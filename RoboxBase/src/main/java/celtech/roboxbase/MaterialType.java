package celtech.roboxbase;

/**
 *
 * @author Ian
 */
public enum MaterialType
{
    /**
     * Acrylonitrile Butadiene Styrene
     */
    ABS("ABS", 1.05),
    /**
     * Acrylonitrile Styrene Acrylate
     */
	ASA("ASA", 1.11),
    /**
     * Methyl Methacrylate Acrylonitrile Butadiene Styrene
     */
    MBS("mABS", 1.08),
    /**
     * Polylactic Acid
     */
    PLA("PLA", 1.24),
    /**
     * Polyamide 12
     */
    P12("PA12", 1.01),
    /**
     * Polyamide 6
     */
    PA6("PA6", 1.01),
    /**
     * Polyamide 6/66 Copolymer
     */
    N66("PA6/66", 1.12),
    /**
     * High-Impact Polystyrene
     */
    HIP("HIPS", 1.04),
    /**
     * Special
     */
    SPC("Special", 1.0),
    /**
     * Co-Polyester
     */
    CPE("CO-PET", 1.23),
    /**
     * Butenediol Vinyl Alcohol
     */
    BVA("BVOH", 1.14),   
    /**
     * Thermoplastic Polyurethane
     */
    TPU("TPU", 1.24),
    /**
     * Polycarbonate
     */
    PCP("PC", 1.20),
    /**
     * Polycarbonate/ABS Alloy
     */
    PCB("PC-ABS", 1.15),
    /**
     * Polyvinyl Butyral
     */
    PVB("PVB", 1.16),
    /**
     * Polycaprolactone
     */
    PCL("PCL", 1.20),
    /**
     * Polyethylene Terephthalate (glycol-modified)
     */
    PTG("PETG", 1.27),
    /**
     * Polyvinyl Alcohol
     */
    PVA("PVOH", 1.19),   
    /**
     * Polylactic Acid/Polyhydroxyalkanoate Alloy
     */
    PHA("PLA/PHA", 1.31),   
    /**
     * Polypropylene
     */
    PPR("PP", 0.90),   
    /**
     * Polyether Ether Ketone
     */
    PEK("PEEK", 1.30),   
    /**
     * Polyether Ketone Ketone
     */
    PKK("PEKK", 1.27),   
    /**
     * Polyphenylsulfone
     */
    PSF("PPSU", 1.29),   
    /**
     * Polyphenylene Sulfide
     */
    PPS("PPS", 1.34),   
    /**
     * Polysulfone
     */
    PSU("PSU", 1.24),   
    /**
     * Polyetherimide
     */
    PEI("PEI", 1.30),   
    /**
     * Polyvinylidene Fluoride
     */
    PVD("PVDF", 1.78),   
    /**
     * Thermoplastic Co-Polyester
     */
    TPC("TPC", 1.13);

    private String friendlyName;
    /**
     * Approximate material density in g / cm^3.
     */
    private double density;

    private MaterialType(String friendlyName, Double density)
    {
        this.friendlyName = friendlyName;
        this.density = density;
    }

    /**
     *
     * @return
     */
    public String getFriendlyName()
    {
        return friendlyName;
    }

    public double getDensity()
    {
        return density;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        return friendlyName;
    }
}
