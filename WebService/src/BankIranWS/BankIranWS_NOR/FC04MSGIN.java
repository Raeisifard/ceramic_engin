
package BankIranWS.BankIranWS_NOR;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FC04MSGIN complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC04MSGIN">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://v1.bankiran.org}gRETFCHDR"/>
 *         &lt;element name="IN_CUSNO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IN_IDNUM">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IN_ACNO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="13"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IN_ID02" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="11"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IN_POSTCODE" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IN_NATIONAL_ID" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="15"/>
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
@XmlType(name = "FC04MSGIN", propOrder = {
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
    "incusno",
    "inidnum",
    "inacno",
    "inid02",
    "inpostcode",
    "innationalid"
})
public class FC04MSGIN {

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
    @XmlElement(name = "IN_CUSNO", required = true, defaultValue = " ", nillable = true)
    protected String incusno;
    @XmlElement(name = "IN_IDNUM", required = true, defaultValue = " ", nillable = true)
    protected String inidnum;
    @XmlElementRef(name = "IN_ACNO", type = JAXBElement.class, required = false)
    protected JAXBElement<String> inacno;
    @XmlElementRef(name = "IN_ID02", type = JAXBElement.class, required = false)
    protected JAXBElement<String> inid02;
    @XmlElementRef(name = "IN_POSTCODE", type = JAXBElement.class, required = false)
    protected JAXBElement<String> inpostcode;
    @XmlElementRef(name = "IN_NATIONAL_ID", type = JAXBElement.class, required = false)
    protected JAXBElement<String> innationalid;

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
     * Gets the value of the incusno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINCUSNO() {
        return incusno;
    }

    /**
     * Sets the value of the incusno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINCUSNO(String value) {
        this.incusno = value;
    }

    /**
     * Gets the value of the inidnum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINIDNUM() {
        return inidnum;
    }

    /**
     * Sets the value of the inidnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINIDNUM(String value) {
        this.inidnum = value;
    }

    /**
     * Gets the value of the inacno property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getINACNO() {
        return inacno;
    }

    /**
     * Sets the value of the inacno property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setINACNO(JAXBElement<String> value) {
        this.inacno = value;
    }

    /**
     * Gets the value of the inid02 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getINID02() {
        return inid02;
    }

    /**
     * Sets the value of the inid02 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setINID02(JAXBElement<String> value) {
        this.inid02 = value;
    }

    /**
     * Gets the value of the inpostcode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getINPOSTCODE() {
        return inpostcode;
    }

    /**
     * Sets the value of the inpostcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setINPOSTCODE(JAXBElement<String> value) {
        this.inpostcode = value;
    }

    /**
     * Gets the value of the innationalid property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getINNATIONALID() {
        return innationalid;
    }

    /**
     * Sets the value of the innationalid property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setINNATIONALID(JAXBElement<String> value) {
        this.innationalid = value;
    }

}