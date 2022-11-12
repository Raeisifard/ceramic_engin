
package BankIranWS.BankIranWS_NOR;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FC02MSGIN complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC02MSGIN">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://v1.bankiran.org}gRETFCHDR"/>
 *         &lt;element name="FC_FC46_FLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FC_CHEQ_NO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FC_BANK_CODE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FC02MSGIN", propOrder = {
    "fctranid",
    "fcctrlid",
    "fcbranchno",
    "fcwsid",
    "fclterm",
    "fcuserid",
    "fcseqno",
    "fcdate",
    "fctime",
    "fcmodeflag",
    "fcstatusflag",
    "fcovrhflag",
    "fcauthofficer",
    "fctranlink",
    "fcfc46FLAG",
    "fccheqno",
    "fcbankcode"
})
public class FC02MSGIN {

    @XmlElement(name = "FC_TRAN_ID", required = true, nillable = true)
    protected String fctranid;
    @XmlElement(name = "FC_CTRL_ID", required = true, nillable = true)
    protected String fcctrlid;
    @XmlElement(name = "FC_BRANCH_NO", required = true, defaultValue = "0", nillable = true)
    protected String fcbranchno;
    @XmlElement(name = "FC_WSID", required = true, nillable = true)
    protected String fcwsid;
    @XmlElement(name = "FC_LTERM", required = true, nillable = true)
    protected String fclterm;
    @XmlElement(name = "FC_USERID", required = true, nillable = true)
    protected String fcuserid;
    @XmlElement(name = "FC_SEQNO", required = true, nillable = true)
    protected String fcseqno;
    @XmlElement(name = "FC_DATE", required = true, nillable = true)
    protected String fcdate;
    @XmlElement(name = "FC_TIME", required = true, nillable = true)
    protected String fctime;
    @XmlElement(name = "FC_MODE_FLAG", required = true, defaultValue = "O", nillable = true)
    protected String fcmodeflag;
    @XmlElement(name = "FC_STATUS_FLAG", required = true, defaultValue = "N", nillable = true)
    protected String fcstatusflag;
    @XmlElement(name = "FC_OVRH_FLAG", required = true, defaultValue = "00", nillable = true)
    protected String fcovrhflag;
    @XmlElement(name = "FC_AUTH_OFFICER", required = true, nillable = true)
    protected String fcauthofficer;
    @XmlElement(name = "FC_TRAN_LINK", required = true, defaultValue = "0")
    protected String fctranlink;
    @XmlElement(name = "FC_FC46_FLAG", required = true, nillable = true)
    protected String fcfc46FLAG;
    @XmlElement(name = "FC_CHEQ_NO", required = true, nillable = true)
    protected String fccheqno;
    @XmlElement(name = "FC_BANK_CODE", required = true, nillable = true)
    protected String fcbankcode;

    /**
     * Gets the value of the fctranid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCTRANID() {
        return fctranid;
    }

    /**
     * Sets the value of the fctranid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCTRANID(String value) {
        this.fctranid = value;
    }

    /**
     * Gets the value of the fcctrlid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCCTRLID() {
        return fcctrlid;
    }

    /**
     * Sets the value of the fcctrlid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCCTRLID(String value) {
        this.fcctrlid = value;
    }

    /**
     * Gets the value of the fcbranchno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCBRANCHNO() {
        return fcbranchno;
    }

    /**
     * Sets the value of the fcbranchno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCBRANCHNO(String value) {
        this.fcbranchno = value;
    }

    /**
     * Gets the value of the fcwsid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCWSID() {
        return fcwsid;
    }

    /**
     * Sets the value of the fcwsid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCWSID(String value) {
        this.fcwsid = value;
    }

    /**
     * Gets the value of the fclterm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCLTERM() {
        return fclterm;
    }

    /**
     * Sets the value of the fclterm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCLTERM(String value) {
        this.fclterm = value;
    }

    /**
     * Gets the value of the fcuserid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCUSERID() {
        return fcuserid;
    }

    /**
     * Sets the value of the fcuserid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCUSERID(String value) {
        this.fcuserid = value;
    }

    /**
     * Gets the value of the fcseqno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCSEQNO() {
        return fcseqno;
    }

    /**
     * Sets the value of the fcseqno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCSEQNO(String value) {
        this.fcseqno = value;
    }

    /**
     * Gets the value of the fcdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCDATE() {
        return fcdate;
    }

    /**
     * Sets the value of the fcdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCDATE(String value) {
        this.fcdate = value;
    }

    /**
     * Gets the value of the fctime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCTIME() {
        return fctime;
    }

    /**
     * Sets the value of the fctime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCTIME(String value) {
        this.fctime = value;
    }

    /**
     * Gets the value of the fcmodeflag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCMODEFLAG() {
        return fcmodeflag;
    }

    /**
     * Sets the value of the fcmodeflag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCMODEFLAG(String value) {
        this.fcmodeflag = value;
    }

    /**
     * Gets the value of the fcstatusflag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCSTATUSFLAG() {
        return fcstatusflag;
    }

    /**
     * Sets the value of the fcstatusflag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCSTATUSFLAG(String value) {
        this.fcstatusflag = value;
    }

    /**
     * Gets the value of the fcovrhflag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCOVRHFLAG() {
        return fcovrhflag;
    }

    /**
     * Sets the value of the fcovrhflag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCOVRHFLAG(String value) {
        this.fcovrhflag = value;
    }

    /**
     * Gets the value of the fcauthofficer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCAUTHOFFICER() {
        return fcauthofficer;
    }

    /**
     * Sets the value of the fcauthofficer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCAUTHOFFICER(String value) {
        this.fcauthofficer = value;
    }

    /**
     * Gets the value of the fctranlink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCTRANLINK() {
        return fctranlink;
    }

    /**
     * Sets the value of the fctranlink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCTRANLINK(String value) {
        this.fctranlink = value;
    }

    /**
     * Gets the value of the fcfc46FLAG property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCFC46FLAG() {
        return fcfc46FLAG;
    }

    /**
     * Sets the value of the fcfc46FLAG property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCFC46FLAG(String value) {
        this.fcfc46FLAG = value;
    }

    /**
     * Gets the value of the fccheqno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCCHEQNO() {
        return fccheqno;
    }

    /**
     * Sets the value of the fccheqno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCCHEQNO(String value) {
        this.fccheqno = value;
    }

    /**
     * Gets the value of the fcbankcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCBANKCODE() {
        return fcbankcode;
    }

    /**
     * Sets the value of the fcbankcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCBANKCODE(String value) {
        this.fcbankcode = value;
    }

}
