package edu.postech.csed332.team3.markdowndoc.converter;

import edu.postech.csed332.team3.markdowndoc.converter.TemplateUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateUtilTest {

    @Test
    void headerTest() {
        try {
            assertEquals("<!DOCTYPE html><html><head> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> <title>MarkdownDoc</title> <style>html{font-family: Arial, Helvetica, sans-serif; font-size: 16px; padding: 1rem;}h1{margin-top: 0.5rem;}h1 + div{margin-top: -0.5rem;}h2{font-style: italic; color: #555555; margin-top: 2rem;}h3{font-family:'Courier New', Courier, monospace;}h5{margin-bottom: -1rem; color: #777777;}.pkg{color: #777777; margin-top: 1rem;}table{border-collapse: collapse; margin: 1rem 0; width: 100%;}td{border: 1px solid #aaaaaa; border-width: 1px 0; padding: 0.25rem 1rem;}td table td{border-width: 1px;}p{line-height: 1.6;}.alert{color: #FF3333;}a{text-decoration: none; font-weight: 500; color: #0000FF;}a:hover{text-decoration: underline; cursor: pointer;}.all{margin-left: 1rem; line-height: 1.2; font-family: 'Courier New', Courier, monospace;}.credits{text-align: right; margin-top: 2rem; color: #555555;}</style></head><body>\n",
                    TemplateUtil.header());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void footerTest() {
        try {
            assertEquals("<div class=\"credits\"> Generated with MarkdownDoc </div><script type=\"text/javascript\">document.addEventListener(\"DOMContentLoaded\", function(){var links=document.getElementsByTagName('a'); for (var i=links.length - 1; i >=0; --i){links[i].addEventListener(\"click\", function(){var id=this.getAttribute('id'); var qn=document.getElementById('pkg').innerText + \".\" + document.getElementById('class').innerText; if (id){console.log(id + \";\" + qn);}});}}); function sortTable(method){var table, rows, switching, i, x, y, shouldSwitch, dir; table=document.getElementById('table'); switching=true; while (switching){switching=false; rows=table.rows; for (i=2; i < rows.length - 1; i++){shouldSwitch=false; x=rows[i].getElementsByTagName(\"td\")[0]; y=rows[i + 1].getElementsByTagName(\"td\")[0]; if (x.getAttribute(\"data-type\") !=method){if (y.getAttribute(\"data-type\")==method){shouldSwitch=true; break;}}}if (shouldSwitch){rows[i].parentNode.insertBefore(rows[i + 1], rows[i]); switching=true;}}}</script></body></html>\n",
                    TemplateUtil.footer());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void allClassesTest() {
        assertEquals("<h2>All classes</h2><div class=\"all\"><a id=\"c-Class1\">Class1</a><br><a id=\"c-Class2\">Class2</a><br><a id=\"c-Class3\">Class3</a><br></div>",
                TemplateUtil.allClasses(List.of("Class1", "Class2", "Class3")));

    }
}