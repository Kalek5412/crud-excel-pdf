package com.kalek.crud.service;


import com.kalek.crud.models.Producto;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;


@Service
public class ExportProductoService {
    private  List<Producto> listaProductos;
    public ExportProductoService(List<Producto> listaProductos){
    super();
    this.listaProductos=listaProductos;
    }

    private void escribirCabeceraTabla(PdfPTable tabla){
        PdfPCell celda = new PdfPCell();

        celda.setBackgroundColor(Color.BLUE);
        celda.setPadding(5);

        Font fuente = FontFactory.getFont(FontFactory.HELVETICA);
        fuente.setColor(Color.WHITE);

        celda.setPhrase(new Phrase("ID", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Nombre", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Precio", fuente));
        tabla.addCell(celda);
    }

    private void escribirDatosDeLaTabla(PdfPTable tabla) {
        for (Producto producto : listaProductos) {
            tabla.addCell(String.valueOf(producto.getId()));
            tabla.addCell(producto.getNombre());
            tabla.addCell(String.valueOf(producto.getPrecio()));
        }

    }

    public void exportar(HttpServletResponse response) throws DocumentException, IOException {
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, response.getOutputStream());

        documento.open();

        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuente.setColor(Color.BLUE);
        fuente.setSize(18);

        Paragraph titulo = new Paragraph("Lista de empleados", fuente);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(titulo);

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(15);
        tabla.setWidths(new float[] { 1f, 2.3f, 2.3f });
        tabla.setWidthPercentage(110);

        escribirCabeceraTabla(tabla);
        escribirDatosDeLaTabla(tabla);

        documento.add(tabla);
        documento.close();
    }

}
