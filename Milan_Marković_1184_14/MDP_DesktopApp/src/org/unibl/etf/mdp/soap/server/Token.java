/**
 * Token.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unibl.etf.mdp.soap.server;

public class Token  implements java.io.Serializable {
    private java.lang.Long expTime;

    private java.lang.String header;

    private java.lang.String payload;

    private java.lang.String sign;

    private java.lang.String subject;

    public Token() {
    }

    public Token(
           java.lang.Long expTime,
           java.lang.String header,
           java.lang.String payload,
           java.lang.String sign,
           java.lang.String subject) {
           this.expTime = expTime;
           this.header = header;
           this.payload = payload;
           this.sign = sign;
           this.subject = subject;
    }


    /**
     * Gets the expTime value for this Token.
     * 
     * @return expTime
     */
    public java.lang.Long getExpTime() {
        return expTime;
    }


    /**
     * Sets the expTime value for this Token.
     * 
     * @param expTime
     */
    public void setExpTime(java.lang.Long expTime) {
        this.expTime = expTime;
    }


    /**
     * Gets the header value for this Token.
     * 
     * @return header
     */
    public java.lang.String getHeader() {
        return header;
    }


    /**
     * Sets the header value for this Token.
     * 
     * @param header
     */
    public void setHeader(java.lang.String header) {
        this.header = header;
    }


    /**
     * Gets the payload value for this Token.
     * 
     * @return payload
     */
    public java.lang.String getPayload() {
        return payload;
    }


    /**
     * Sets the payload value for this Token.
     * 
     * @param payload
     */
    public void setPayload(java.lang.String payload) {
        this.payload = payload;
    }


    /**
     * Gets the sign value for this Token.
     * 
     * @return sign
     */
    public java.lang.String getSign() {
        return sign;
    }


    /**
     * Sets the sign value for this Token.
     * 
     * @param sign
     */
    public void setSign(java.lang.String sign) {
        this.sign = sign;
    }


    /**
     * Gets the subject value for this Token.
     * 
     * @return subject
     */
    public java.lang.String getSubject() {
        return subject;
    }


    /**
     * Sets the subject value for this Token.
     * 
     * @param subject
     */
    public void setSubject(java.lang.String subject) {
        this.subject = subject;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Token)) return false;
        Token other = (Token) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.expTime==null && other.getExpTime()==null) || 
             (this.expTime!=null &&
              this.expTime.equals(other.getExpTime()))) &&
            ((this.header==null && other.getHeader()==null) || 
             (this.header!=null &&
              this.header.equals(other.getHeader()))) &&
            ((this.payload==null && other.getPayload()==null) || 
             (this.payload!=null &&
              this.payload.equals(other.getPayload()))) &&
            ((this.sign==null && other.getSign()==null) || 
             (this.sign!=null &&
              this.sign.equals(other.getSign()))) &&
            ((this.subject==null && other.getSubject()==null) || 
             (this.subject!=null &&
              this.subject.equals(other.getSubject())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getExpTime() != null) {
            _hashCode += getExpTime().hashCode();
        }
        if (getHeader() != null) {
            _hashCode += getHeader().hashCode();
        }
        if (getPayload() != null) {
            _hashCode += getPayload().hashCode();
        }
        if (getSign() != null) {
            _hashCode += getSign().hashCode();
        }
        if (getSubject() != null) {
            _hashCode += getSubject().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Token.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://server.soap.mdp.etf.unibl.org", "Token"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://server.soap.mdp.etf.unibl.org", "expTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("header");
        elemField.setXmlName(new javax.xml.namespace.QName("http://server.soap.mdp.etf.unibl.org", "header"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payload");
        elemField.setXmlName(new javax.xml.namespace.QName("http://server.soap.mdp.etf.unibl.org", "payload"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sign");
        elemField.setXmlName(new javax.xml.namespace.QName("http://server.soap.mdp.etf.unibl.org", "sign"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://server.soap.mdp.etf.unibl.org", "subject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
