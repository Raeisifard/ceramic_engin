
package BankIranWS.BankIranWS_NOR;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Fc87msgout_out__customer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Fc87msgout_out__customer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://v1.bankiran.org}fc87msgout_out__customer"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Fc87msgout_out__customer", propOrder = {
    "outcusno",
    "outname",
    "outfamily",
    "outfathername",
    "outidnumber",
    "outbirthdate",
    "outmobileno",
    "outnatcd",
    "outcustype",
    "outlangcd",
    "outbranchno",
    "outmarstat",
    "outdateopn",
    "outnatcode",
    "outrestat",
    "outcomrgno",
    "outcusubtyp",
    "outsexcode",
    "outenname",
    "outenfamily",
    "outprofcode",
    "outissuedtown",
    "outissueddate",
    "outregioncode",
    "outeduclvl",
    "outidtype",
    "outisucap",
    "outhomphone",
    "outbusphone",
    "outpostcode",
    "outserinum",
    "outserialnum",
    "outnamaddr1",
    "outnamaddr2",
    "outnamaddr3",
    "outnamaddr4"
})
public class Fc87MsgoutOutCustomer {

    @XmlElement(name = "OUT_CUSNO", required = true, nillable = true)
    protected String outcusno;
    @XmlElement(name = "OUT_NAME", required = true, nillable = true)
    protected String outname;
    @XmlElement(name = "OUT_FAMILY", required = true, nillable = true)
    protected String outfamily;
    @XmlElement(name = "OUT_FATHER_NAME", required = true, nillable = true)
    protected String outfathername;
    @XmlElement(name = "OUT_IDNUMBER", required = true, nillable = true)
    protected String outidnumber;
    @XmlElement(name = "OUT_BIRTH_DATE", required = true, nillable = true)
    protected String outbirthdate;
    @XmlElement(name = "OUT_MOBILE_NO", required = true, nillable = true)
    protected String outmobileno;
    @XmlElement(name = "OUT_NATCD", required = true, nillable = true)
    protected String outnatcd;
    @XmlElement(name = "OUT_CUSTYPE", required = true, nillable = true)
    protected String outcustype;
    @XmlElement(name = "OUT_LANGCD", required = true, nillable = true)
    protected String outlangcd;
    @XmlElement(name = "OUT_BRANCHNO", required = true, nillable = true)
    protected String outbranchno;
    @XmlElement(name = "OUT_MARSTAT", required = true, nillable = true)
    protected String outmarstat;
    @XmlElement(name = "OUT_DATEOPN", required = true, nillable = true)
    protected String outdateopn;
    @XmlElement(name = "OUT_NAT_CODE", required = true, nillable = true)
    protected String outnatcode;
    @XmlElement(name = "OUT_RESTAT", required = true, nillable = true)
    protected String outrestat;
    @XmlElement(name = "OUT_COMRGNO", required = true, nillable = true)
    protected String outcomrgno;
    @XmlElement(name = "OUT_CUSUBTYP", required = true, nillable = true)
    protected String outcusubtyp;
    @XmlElement(name = "OUT_SEXCODE", required = true, nillable = true)
    protected String outsexcode;
    @XmlElement(name = "OUT_EN_NAME", required = true, nillable = true)
    protected String outenname;
    @XmlElement(name = "OUT_EN_FAMILY", required = true, nillable = true)
    protected String outenfamily;
    @XmlElement(name = "OUT_PROFCODE", required = true, nillable = true)
    protected String outprofcode;
    @XmlElement(name = "OUT_ISSUED_TOWN", required = true, nillable = true)
    protected String outissuedtown;
    @XmlElement(name = "OUT_ISSUED_DATE", required = true, nillable = true)
    protected String outissueddate;
    @XmlElement(name = "OUT_REGION_CODE", required = true, nillable = true)
    protected String outregioncode;
    @XmlElement(name = "OUT_EDUC_LVL", required = true, nillable = true)
    protected String outeduclvl;
    @XmlElement(name = "OUT_IDTYPE", required = true, nillable = true)
    protected String outidtype;
    @XmlElement(name = "OUT_ISUCAP", required = true, nillable = true)
    protected String outisucap;
    @XmlElement(name = "OUT_HOMPHONE", required = true)
    protected String outhomphone;
    @XmlElement(name = "OUT_BUSPHONE", required = true)
    protected String outbusphone;
    @XmlElement(name = "OUT_POSTCODE", required = true)
    protected String outpostcode;
    @XmlElement(name = "OUT_SERI_NUM", required = true)
    protected String outserinum;
    @XmlElement(name = "OUT_SERIAL_NUM", required = true)
    protected String outserialnum;
    @XmlElement(name = "OUT_NAMADDR1", required = true)
    protected String outnamaddr1;
    @XmlElement(name = "OUT_NAMADDR2", required = true)
    protected String outnamaddr2;
    @XmlElement(name = "OUT_NAMADDR3", required = true)
    protected String outnamaddr3;
    @XmlElement(name = "OUT_NAMADDR4", required = true)
    protected String outnamaddr4;

    /**
     * Gets the value of the outcusno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTCUSNO() {
        return outcusno;
    }

    /**
     * Sets the value of the outcusno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTCUSNO(String value) {
        this.outcusno = value;
    }

    /**
     * Gets the value of the outname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTNAME() {
        return outname;
    }

    /**
     * Sets the value of the outname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTNAME(String value) {
        this.outname = value;
    }

    /**
     * Gets the value of the outfamily property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTFAMILY() {
        return outfamily;
    }

    /**
     * Sets the value of the outfamily property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTFAMILY(String value) {
        this.outfamily = value;
    }

    /**
     * Gets the value of the outfathername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTFATHERNAME() {
        return outfathername;
    }

    /**
     * Sets the value of the outfathername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTFATHERNAME(String value) {
        this.outfathername = value;
    }

    /**
     * Gets the value of the outidnumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTIDNUMBER() {
        return outidnumber;
    }

    /**
     * Sets the value of the outidnumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTIDNUMBER(String value) {
        this.outidnumber = value;
    }

    /**
     * Gets the value of the outbirthdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTBIRTHDATE() {
        return outbirthdate;
    }

    /**
     * Sets the value of the outbirthdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTBIRTHDATE(String value) {
        this.outbirthdate = value;
    }

    /**
     * Gets the value of the outmobileno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTMOBILENO() {
        return outmobileno;
    }

    /**
     * Sets the value of the outmobileno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTMOBILENO(String value) {
        this.outmobileno = value;
    }

    /**
     * Gets the value of the outnatcd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTNATCD() {
        return outnatcd;
    }

    /**
     * Sets the value of the outnatcd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTNATCD(String value) {
        this.outnatcd = value;
    }

    /**
     * Gets the value of the outcustype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTCUSTYPE() {
        return outcustype;
    }

    /**
     * Sets the value of the outcustype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTCUSTYPE(String value) {
        this.outcustype = value;
    }

    /**
     * Gets the value of the outlangcd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTLANGCD() {
        return outlangcd;
    }

    /**
     * Sets the value of the outlangcd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTLANGCD(String value) {
        this.outlangcd = value;
    }

    /**
     * Gets the value of the outbranchno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTBRANCHNO() {
        return outbranchno;
    }

    /**
     * Sets the value of the outbranchno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTBRANCHNO(String value) {
        this.outbranchno = value;
    }

    /**
     * Gets the value of the outmarstat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTMARSTAT() {
        return outmarstat;
    }

    /**
     * Sets the value of the outmarstat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTMARSTAT(String value) {
        this.outmarstat = value;
    }

    /**
     * Gets the value of the outdateopn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTDATEOPN() {
        return outdateopn;
    }

    /**
     * Sets the value of the outdateopn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTDATEOPN(String value) {
        this.outdateopn = value;
    }

    /**
     * Gets the value of the outnatcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTNATCODE() {
        return outnatcode;
    }

    /**
     * Sets the value of the outnatcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTNATCODE(String value) {
        this.outnatcode = value;
    }

    /**
     * Gets the value of the outrestat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTRESTAT() {
        return outrestat;
    }

    /**
     * Sets the value of the outrestat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTRESTAT(String value) {
        this.outrestat = value;
    }

    /**
     * Gets the value of the outcomrgno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTCOMRGNO() {
        return outcomrgno;
    }

    /**
     * Sets the value of the outcomrgno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTCOMRGNO(String value) {
        this.outcomrgno = value;
    }

    /**
     * Gets the value of the outcusubtyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTCUSUBTYP() {
        return outcusubtyp;
    }

    /**
     * Sets the value of the outcusubtyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTCUSUBTYP(String value) {
        this.outcusubtyp = value;
    }

    /**
     * Gets the value of the outsexcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTSEXCODE() {
        return outsexcode;
    }

    /**
     * Sets the value of the outsexcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTSEXCODE(String value) {
        this.outsexcode = value;
    }

    /**
     * Gets the value of the outenname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTENNAME() {
        return outenname;
    }

    /**
     * Sets the value of the outenname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTENNAME(String value) {
        this.outenname = value;
    }

    /**
     * Gets the value of the outenfamily property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTENFAMILY() {
        return outenfamily;
    }

    /**
     * Sets the value of the outenfamily property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTENFAMILY(String value) {
        this.outenfamily = value;
    }

    /**
     * Gets the value of the outprofcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTPROFCODE() {
        return outprofcode;
    }

    /**
     * Sets the value of the outprofcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTPROFCODE(String value) {
        this.outprofcode = value;
    }

    /**
     * Gets the value of the outissuedtown property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTISSUEDTOWN() {
        return outissuedtown;
    }

    /**
     * Sets the value of the outissuedtown property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTISSUEDTOWN(String value) {
        this.outissuedtown = value;
    }

    /**
     * Gets the value of the outissueddate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTISSUEDDATE() {
        return outissueddate;
    }

    /**
     * Sets the value of the outissueddate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTISSUEDDATE(String value) {
        this.outissueddate = value;
    }

    /**
     * Gets the value of the outregioncode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTREGIONCODE() {
        return outregioncode;
    }

    /**
     * Sets the value of the outregioncode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTREGIONCODE(String value) {
        this.outregioncode = value;
    }

    /**
     * Gets the value of the outeduclvl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTEDUCLVL() {
        return outeduclvl;
    }

    /**
     * Sets the value of the outeduclvl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTEDUCLVL(String value) {
        this.outeduclvl = value;
    }

    /**
     * Gets the value of the outidtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTIDTYPE() {
        return outidtype;
    }

    /**
     * Sets the value of the outidtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTIDTYPE(String value) {
        this.outidtype = value;
    }

    /**
     * Gets the value of the outisucap property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTISUCAP() {
        return outisucap;
    }

    /**
     * Sets the value of the outisucap property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTISUCAP(String value) {
        this.outisucap = value;
    }

    /**
     * Gets the value of the outhomphone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTHOMPHONE() {
        return outhomphone;
    }

    /**
     * Sets the value of the outhomphone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTHOMPHONE(String value) {
        this.outhomphone = value;
    }

    /**
     * Gets the value of the outbusphone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTBUSPHONE() {
        return outbusphone;
    }

    /**
     * Sets the value of the outbusphone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTBUSPHONE(String value) {
        this.outbusphone = value;
    }

    /**
     * Gets the value of the outpostcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTPOSTCODE() {
        return outpostcode;
    }

    /**
     * Sets the value of the outpostcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTPOSTCODE(String value) {
        this.outpostcode = value;
    }

    /**
     * Gets the value of the outserinum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTSERINUM() {
        return outserinum;
    }

    /**
     * Sets the value of the outserinum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTSERINUM(String value) {
        this.outserinum = value;
    }

    /**
     * Gets the value of the outserialnum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTSERIALNUM() {
        return outserialnum;
    }

    /**
     * Sets the value of the outserialnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTSERIALNUM(String value) {
        this.outserialnum = value;
    }

    /**
     * Gets the value of the outnamaddr1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTNAMADDR1() {
        return outnamaddr1;
    }

    /**
     * Sets the value of the outnamaddr1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTNAMADDR1(String value) {
        this.outnamaddr1 = value;
    }

    /**
     * Gets the value of the outnamaddr2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTNAMADDR2() {
        return outnamaddr2;
    }

    /**
     * Sets the value of the outnamaddr2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTNAMADDR2(String value) {
        this.outnamaddr2 = value;
    }

    /**
     * Gets the value of the outnamaddr3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTNAMADDR3() {
        return outnamaddr3;
    }

    /**
     * Sets the value of the outnamaddr3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTNAMADDR3(String value) {
        this.outnamaddr3 = value;
    }

    /**
     * Gets the value of the outnamaddr4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTNAMADDR4() {
        return outnamaddr4;
    }

    /**
     * Sets the value of the outnamaddr4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTNAMADDR4(String value) {
        this.outnamaddr4 = value;
    }

}
