module com.jovisco.pdf {
    requires static lombok;
    requires org.slf4j;
    requires org.apache.pdfbox;
    requires org.apache.xmpbox;
    requires java.xml;
    requires library;

    exports com.jovisco.pdf.shared;
}