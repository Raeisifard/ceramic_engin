
package BankIranWS.BankIranWS_NOR;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the BankIranWS.BankIranWS_NOR package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _MsgMB60FROMHOST_QNAME = new QName("http://v1.bankiran.org", "msg_MB60FROMHOST");
    private final static QName _MsgFC02TOHOST_QNAME = new QName("http://v1.bankiran.org", "msg_FC02TOHOST");
    private final static QName _Envelope_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
    private final static QName _Fault_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
    private final static QName _MsgMB60TOHOST_QNAME = new QName("http://v1.bankiran.org", "msg_MB60TOHOST");
    private final static QName _MsgFC44FROMHOST_QNAME = new QName("http://v1.bankiran.org", "msg_FC44FROMHOST");
    private final static QName _MsgFC44TOHOST_QNAME = new QName("http://v1.bankiran.org", "msg_FC44TOHOST");
    private final static QName _MsgFC04TOHOST_QNAME = new QName("http://v1.bankiran.org", "msg_FC04TOHOST");
    private final static QName _MsgFlt_QNAME = new QName("http://v1.bankiran.org", "msg_Flt");
    private final static QName _MsgFC04FROMHOST_QNAME = new QName("http://v1.bankiran.org", "msg_FC04FROMHOST");
    private final static QName _Body_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body");
    private final static QName _MsgFC87FROMHOST_QNAME = new QName("http://v1.bankiran.org", "msg_FC87FROMHOST");
    private final static QName _MsgFC87TOHOST_QNAME = new QName("http://v1.bankiran.org", "msg_FC87TOHOST");
    private final static QName _MsgFC02FROMHOST_QNAME = new QName("http://v1.bankiran.org", "msg_FC02FROMHOST");
    private final static QName _Header_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Header");
    private final static QName _FC04MSGININACNO_QNAME = new QName("", "IN_ACNO");
    private final static QName _FC04MSGININID02_QNAME = new QName("", "IN_ID02");
    private final static QName _FC04MSGININNATIONALID_QNAME = new QName("", "IN_NATIONAL_ID");
    private final static QName _FC04MSGININPOSTCODE_QNAME = new QName("", "IN_POSTCODE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: BankIranWS.BankIranWS_NOR
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Fault }
     * 
     */
    public Fault createFault() {
        return new Fault();
    }

    /**
     * Create an instance of {@link MB60MSGOUT }
     * 
     */
    public MB60MSGOUT createMB60MSGOUT() {
        return new MB60MSGOUT();
    }

    /**
     * Create an instance of {@link FC04MSGOUT }
     * 
     */
    public FC04MSGOUT createFC04MSGOUT() {
        return new FC04MSGOUT();
    }

    /**
     * Create an instance of {@link FC02MSGIN }
     * 
     */
    public FC02MSGIN createFC02MSGIN() {
        return new FC02MSGIN();
    }

    /**
     * Create an instance of {@link FC87MSGOUT }
     * 
     */
    public FC87MSGOUT createFC87MSGOUT() {
        return new FC87MSGOUT();
    }

    /**
     * Create an instance of {@link FC87MSGIN }
     * 
     */
    public FC87MSGIN createFC87MSGIN() {
        return new FC87MSGIN();
    }

    /**
     * Create an instance of {@link FC02MSGOUT }
     * 
     */
    public FC02MSGOUT createFC02MSGOUT() {
        return new FC02MSGOUT();
    }

    /**
     * Create an instance of {@link FC44MSGIN }
     * 
     */
    public FC44MSGIN createFC44MSGIN() {
        return new FC44MSGIN();
    }

    /**
     * Create an instance of {@link MB60MSGIN }
     * 
     */
    public MB60MSGIN createMB60MSGIN() {
        return new MB60MSGIN();
    }

    /**
     * Create an instance of {@link FC44MSGOUT }
     * 
     */
    public FC44MSGOUT createFC44MSGOUT() {
        return new FC44MSGOUT();
    }

    /**
     * Create an instance of {@link TFlt }
     * 
     */
    public TFlt createTFlt() {
        return new TFlt();
    }

    /**
     * Create an instance of {@link FC04MSGIN }
     * 
     */
    public FC04MSGIN createFC04MSGIN() {
        return new FC04MSGIN();
    }

    /**
     * Create an instance of {@link Fc87MsgoutOutCustomer }
     * 
     */
    public Fc87MsgoutOutCustomer createFc87MsgoutOutCustomer() {
        return new Fc87MsgoutOutCustomer();
    }

    /**
     * Create an instance of {@link ComplexType }
     * 
     */
    public ComplexType createComplexType() {
        return new ComplexType();
    }

    /**
     * Create an instance of {@link Fc04MsgoutOutAccount }
     * 
     */
    public Fc04MsgoutOutAccount createFc04MsgoutOutAccount() {
        return new Fc04MsgoutOutAccount();
    }

    /**
     * Create an instance of {@link Header }
     * 
     */
    public Header createHeader() {
        return new Header();
    }

    /**
     * Create an instance of {@link Envelope }
     * 
     */
    public Envelope createEnvelope() {
        return new Envelope();
    }

    /**
     * Create an instance of {@link Body }
     * 
     */
    public Body createBody() {
        return new Body();
    }

    /**
     * Create an instance of {@link Detail }
     * 
     */
    public Detail createDetail() {
        return new Detail();
    }

    /**
     * Create an instance of {@link Fault.Faultstring }
     * 
     */
    public Fault.Faultstring createFaultFaultstring() {
        return new Fault.Faultstring();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MB60MSGOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.bankiran.org", name = "msg_MB60FROMHOST")
    public JAXBElement<MB60MSGOUT> createMsgMB60FROMHOST(MB60MSGOUT value) {
        return new JAXBElement<MB60MSGOUT>(_MsgMB60FROMHOST_QNAME, MB60MSGOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FC02MSGIN }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.bankiran.org", name = "msg_FC02TOHOST")
    public JAXBElement<FC02MSGIN> createMsgFC02TOHOST(FC02MSGIN value) {
        return new JAXBElement<FC02MSGIN>(_MsgFC02TOHOST_QNAME, FC02MSGIN.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Envelope }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/soap/envelope/", name = "Envelope")
    public JAXBElement<Envelope> createEnvelope(Envelope value) {
        return new JAXBElement<Envelope>(_Envelope_QNAME, Envelope.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Fault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/soap/envelope/", name = "Fault")
    public JAXBElement<Fault> createFault(Fault value) {
        return new JAXBElement<Fault>(_Fault_QNAME, Fault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MB60MSGIN }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.bankiran.org", name = "msg_MB60TOHOST")
    public JAXBElement<MB60MSGIN> createMsgMB60TOHOST(MB60MSGIN value) {
        return new JAXBElement<MB60MSGIN>(_MsgMB60TOHOST_QNAME, MB60MSGIN.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FC44MSGOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.bankiran.org", name = "msg_FC44FROMHOST")
    public JAXBElement<FC44MSGOUT> createMsgFC44FROMHOST(FC44MSGOUT value) {
        return new JAXBElement<FC44MSGOUT>(_MsgFC44FROMHOST_QNAME, FC44MSGOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FC44MSGIN }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.bankiran.org", name = "msg_FC44TOHOST")
    public JAXBElement<FC44MSGIN> createMsgFC44TOHOST(FC44MSGIN value) {
        return new JAXBElement<FC44MSGIN>(_MsgFC44TOHOST_QNAME, FC44MSGIN.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FC04MSGIN }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.bankiran.org", name = "msg_FC04TOHOST")
    public JAXBElement<FC04MSGIN> createMsgFC04TOHOST(FC04MSGIN value) {
        return new JAXBElement<FC04MSGIN>(_MsgFC04TOHOST_QNAME, FC04MSGIN.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TFlt }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.bankiran.org", name = "msg_Flt")
    public JAXBElement<TFlt> createMsgFlt(TFlt value) {
        return new JAXBElement<TFlt>(_MsgFlt_QNAME, TFlt.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FC04MSGOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.bankiran.org", name = "msg_FC04FROMHOST")
    public JAXBElement<FC04MSGOUT> createMsgFC04FROMHOST(FC04MSGOUT value) {
        return new JAXBElement<FC04MSGOUT>(_MsgFC04FROMHOST_QNAME, FC04MSGOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Body }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/soap/envelope/", name = "Body")
    public JAXBElement<Body> createBody(Body value) {
        return new JAXBElement<Body>(_Body_QNAME, Body.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FC87MSGOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.bankiran.org", name = "msg_FC87FROMHOST")
    public JAXBElement<FC87MSGOUT> createMsgFC87FROMHOST(FC87MSGOUT value) {
        return new JAXBElement<FC87MSGOUT>(_MsgFC87FROMHOST_QNAME, FC87MSGOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FC87MSGIN }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.bankiran.org", name = "msg_FC87TOHOST")
    public JAXBElement<FC87MSGIN> createMsgFC87TOHOST(FC87MSGIN value) {
        return new JAXBElement<FC87MSGIN>(_MsgFC87TOHOST_QNAME, FC87MSGIN.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FC02MSGOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.bankiran.org", name = "msg_FC02FROMHOST")
    public JAXBElement<FC02MSGOUT> createMsgFC02FROMHOST(FC02MSGOUT value) {
        return new JAXBElement<FC02MSGOUT>(_MsgFC02FROMHOST_QNAME, FC02MSGOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Header }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/soap/envelope/", name = "Header")
    public JAXBElement<Header> createHeader(Header value) {
        return new JAXBElement<Header>(_Header_QNAME, Header.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "IN_ACNO", scope = FC04MSGIN.class, defaultValue = " ")
    public JAXBElement<String> createFC04MSGININACNO(String value) {
        return new JAXBElement<String>(_FC04MSGININACNO_QNAME, String.class, FC04MSGIN.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "IN_ID02", scope = FC04MSGIN.class, defaultValue = " ")
    public JAXBElement<String> createFC04MSGININID02(String value) {
        return new JAXBElement<String>(_FC04MSGININID02_QNAME, String.class, FC04MSGIN.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "IN_NATIONAL_ID", scope = FC04MSGIN.class, defaultValue = "000000000000000")
    public JAXBElement<String> createFC04MSGININNATIONALID(String value) {
        return new JAXBElement<String>(_FC04MSGININNATIONALID_QNAME, String.class, FC04MSGIN.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "IN_POSTCODE", scope = FC04MSGIN.class, defaultValue = " ")
    public JAXBElement<String> createFC04MSGININPOSTCODE(String value) {
        return new JAXBElement<String>(_FC04MSGININPOSTCODE_QNAME, String.class, FC04MSGIN.class, value);
    }

}
