
package BankIranWS.BankIranWS_NOR;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FC44MSGIN complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC44MSGIN">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://v1.bankiran.org}gRETFCHDR"/>
 *         &lt;element name="ACCOUNT_NO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="13"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TRAN_AMT">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}long">
 *               &lt;minInclusive value="-999999999999999"/>
 *               &lt;maxInclusive value="999999999999999"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TRAN_KDEQ">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}long">
 *               &lt;minInclusive value="-999999999999999"/>
 *               &lt;maxInclusive value="999999999999999"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="STAT_DESC">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CHQ_NO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="VALUE_DATE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="6"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OPT_INFO1">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="30"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OPT_INFO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="15"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="LANG_CODE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PRINTIND">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ORIG_KEY">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="20"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CHARGE_CODE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CHQ_DATE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="6"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FC_FX_RATE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="999999.9999999"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FC_SALERT">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="999999.9999999"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FC_NATIONAL_ID">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="16"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FC_PHASE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FC_CTR_ID">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="22"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FC_ORIGIN_OF_MONEY">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="34"/>
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
@XmlType(name = "FC44MSGIN", propOrder = {
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
    "accountno",
    "tranamt",
    "trankdeq",
    "statdesc",
    "chqno",
    "valuedate",
    "optinfo1",
    "optinfo",
    "langcode",
    "printind",
    "origkey",
    "chargecode",
    "chqdate",
    "fcfxrate",
    "fcsalert",
    "fcnationalid",
    "fcphase",
    "fcctrid",
    "fcoriginofmoney"
})
public class FC44MSGIN {

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
    @XmlElement(name = "ACCOUNT_NO", required = true, nillable = true)
    protected String accountno;
    @XmlElement(name = "TRAN_AMT", required = true, type = Long.class, nillable = true)
    protected Long tranamt;
    @XmlElement(name = "TRAN_KDEQ", required = true, type = Long.class, nillable = true)
    protected Long trankdeq;
    @XmlElement(name = "STAT_DESC", required = true, nillable = true)
    protected String statdesc;
    @XmlElement(name = "CHQ_NO", required = true, defaultValue = "0", nillable = true)
    protected String chqno;
    @XmlElement(name = "VALUE_DATE", required = true, nillable = true)
    protected String valuedate;
    @XmlElement(name = "OPT_INFO1", required = true, nillable = true)
    protected String optinfo1;
    @XmlElement(name = "OPT_INFO", required = true, nillable = true)
    protected String optinfo;
    @XmlElement(name = "LANG_CODE", required = true, nillable = true)
    protected String langcode;
    @XmlElement(name = "PRINTIND", required = true, nillable = true)
    protected String printind;
    @XmlElement(name = "ORIG_KEY", required = true, nillable = true)
    protected String origkey;
    @XmlElement(name = "CHARGE_CODE", required = true, nillable = true)
    protected String chargecode;
    @XmlElement(name = "CHQ_DATE", required = true, nillable = true)
    protected String chqdate;
    @XmlElement(name = "FC_FX_RATE", required = true, nillable = true)
    protected BigDecimal fcfxrate;
    @XmlElement(name = "FC_SALERT", required = true, nillable = true)
    protected BigDecimal fcsalert;
    @XmlElement(name = "FC_NATIONAL_ID", required = true, nillable = true)
    protected String fcnationalid;
    @XmlElement(name = "FC_PHASE", required = true)
    protected String fcphase;
    @XmlElement(name = "FC_CTR_ID", required = true, nillable = true)
    protected String fcctrid;
    @XmlElement(name = "FC_ORIGIN_OF_MONEY", required = true, nillable = true)
    protected String fcoriginofmoney;

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
     * Gets the value of the accountno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACCOUNTNO() {
        return accountno;
    }

    /**
     * Sets the value of the accountno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACCOUNTNO(String value) {
        this.accountno = value;
    }

    /**
     * Gets the value of the tranamt property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTRANAMT() {
        return tranamt;
    }

    /**
     * Sets the value of the tranamt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTRANAMT(Long value) {
        this.tranamt = value;
    }

    /**
     * Gets the value of the trankdeq property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTRANKDEQ() {
        return trankdeq;
    }

    /**
     * Sets the value of the trankdeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTRANKDEQ(Long value) {
        this.trankdeq = value;
    }

    /**
     * Gets the value of the statdesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTATDESC() {
        return statdesc;
    }

    /**
     * Sets the value of the statdesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTATDESC(String value) {
        this.statdesc = value;
    }

    /**
     * Gets the value of the chqno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCHQNO() {
        return chqno;
    }

    /**
     * Sets the value of the chqno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCHQNO(String value) {
        this.chqno = value;
    }

    /**
     * Gets the value of the valuedate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVALUEDATE() {
        return valuedate;
    }

    /**
     * Sets the value of the valuedate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVALUEDATE(String value) {
        this.valuedate = value;
    }

    /**
     * Gets the value of the optinfo1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOPTINFO1() {
        return optinfo1;
    }

    /**
     * Sets the value of the optinfo1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOPTINFO1(String value) {
        this.optinfo1 = value;
    }

    /**
     * Gets the value of the optinfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOPTINFO() {
        return optinfo;
    }

    /**
     * Sets the value of the optinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOPTINFO(String value) {
        this.optinfo = value;
    }

    /**
     * Gets the value of the langcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLANGCODE() {
        return langcode;
    }

    /**
     * Sets the value of the langcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLANGCODE(String value) {
        this.langcode = value;
    }

    /**
     * Gets the value of the printind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRINTIND() {
        return printind;
    }

    /**
     * Sets the value of the printind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRINTIND(String value) {
        this.printind = value;
    }

    /**
     * Gets the value of the origkey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getORIGKEY() {
        return origkey;
    }

    /**
     * Sets the value of the origkey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setORIGKEY(String value) {
        this.origkey = value;
    }

    /**
     * Gets the value of the chargecode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCHARGECODE() {
        return chargecode;
    }

    /**
     * Sets the value of the chargecode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCHARGECODE(String value) {
        this.chargecode = value;
    }

    /**
     * Gets the value of the chqdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCHQDATE() {
        return chqdate;
    }

    /**
     * Sets the value of the chqdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCHQDATE(String value) {
        this.chqdate = value;
    }

    /**
     * Gets the value of the fcfxrate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFCFXRATE() {
        return fcfxrate;
    }

    /**
     * Sets the value of the fcfxrate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFCFXRATE(BigDecimal value) {
        this.fcfxrate = value;
    }

    /**
     * Gets the value of the fcsalert property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFCSALERT() {
        return fcsalert;
    }

    /**
     * Sets the value of the fcsalert property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFCSALERT(BigDecimal value) {
        this.fcsalert = value;
    }

    /**
     * Gets the value of the fcnationalid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCNATIONALID() {
        return fcnationalid;
    }

    /**
     * Sets the value of the fcnationalid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCNATIONALID(String value) {
        this.fcnationalid = value;
    }

    /**
     * Gets the value of the fcphase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCPHASE() {
        return fcphase;
    }

    /**
     * Sets the value of the fcphase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCPHASE(String value) {
        this.fcphase = value;
    }

    /**
     * Gets the value of the fcctrid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCCTRID() {
        return fcctrid;
    }

    /**
     * Sets the value of the fcctrid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCCTRID(String value) {
        this.fcctrid = value;
    }

    /**
     * Gets the value of the fcoriginofmoney property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCORIGINOFMONEY() {
        return fcoriginofmoney;
    }

    /**
     * Sets the value of the fcoriginofmoney property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCORIGINOFMONEY(String value) {
        this.fcoriginofmoney = value;
    }

}
