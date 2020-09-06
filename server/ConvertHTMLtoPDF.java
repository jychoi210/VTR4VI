package html2pdf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import html2pdf.DefaultFontProvider;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;


public class ConvertHTMLtoPDF{
public void convert(String filename) {
String pdfName = filename.replace(".html", "");
String file = "/home/yoon/pdf/"+pdfName+".pdf";   
 
 PdfWriter pdfWriter = null;       
 try{
   //create a new document   
   Document document = new Document();       
     
   //get Instance of the PDFWriter    
   pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
   document.setPageSize(PageSize.A4);      
document.open();
XMLWorkerHelper helper = XMLWorkerHelper.getInstance();
     
// CSS
CSSResolver cssResolver = new StyleAttrCSSResolver();
CssFile cssFile = helper.getCSS(new FileInputStream("/home/yoon/pdf/pdf.css"));
cssResolver.addCss(cssFile);
     
// HTML, 폰트 설정
XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
fontProvider.register("/home/yoon/malgun.ttf", "MalgunGothic"); // MalgunGothic은 alias,
CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);

HTMLWorker htmlWorker = new HTMLWorker(document);
HashMap<String,Object> interfaceProps = new HashMap<String,Object>();
DefaultFontProvider dfp=new DefaultFontProvider("/home/yoon/malgun.ttf");
   //폰트 파일 설정 (한글 나오게 하기 위해 설정 필요함
   interfaceProps.put(HTMLWorker.FONT_PROVIDER,dfp);
 
HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

PdfWriterPipeline pdf = new PdfWriterPipeline(document, pdfWriter);
HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
 
XMLWorker worker = new XMLWorker(css, true);
XMLParser xmlParser = new XMLParser(worker, Charset.forName("UTF-8"));

FileReader fileReader = null;
   BufferedReader bufferedReader = null;

   fileReader = new FileReader("/home/yoon/html/" + filename );
   bufferedReader = new BufferedReader(fileReader);
   String line;
   StringBuffer sb = new StringBuffer();
   String htmlstr;
   while ((line = bufferedReader.readLine()) != null) {
   	htmlstr = line;
   	StringReader strReader = new StringReader(htmlstr);
   	xmlParser.parse(strReader);
  
   }
   document.close();
  pdfWriter.close();
 }catch (IOException e) {
   	  // TODO Auto-generated catch block
   	  e.printStackTrace();
   }catch(DocumentException de){
   	de.printStackTrace();
   }
   

 }
 }
