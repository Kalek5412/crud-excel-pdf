package com.kalek.crud.service;

import com.kalek.crud.models.Producto;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Service
public class ExportExProductoService {
    private XSSFWorkbook libro;
    private XSSFSheet hoja;
    private List<Producto> listaProductos;

    public ExportExProductoService(List<Producto> listaProductos){
        this.listaProductos=listaProductos;
        libro = new XSSFWorkbook();
        hoja = libro.createSheet("Productos");
    }

    private void escribirCabeceraDeTabla() {
        Row fila = hoja.createRow(0);

        CellStyle estilo = libro.createCellStyle();
        XSSFFont fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setFontHeight(16);
        estilo.setFont(fuente);

        Cell celda = fila.createCell(0);
        celda.setCellValue("ID");
        celda.setCellStyle(estilo);

        celda = fila.createCell(1);
        celda.setCellValue("Nombre");
        celda.setCellStyle(estilo);

        celda = fila.createCell(2);
        celda.setCellValue("Precio");
        celda.setCellStyle(estilo);
    }

    private void escribirDatosDeLaTabla() {
        int numeroFilas = 1;

        CellStyle estilo = libro.createCellStyle();
        XSSFFont fuente = libro.createFont();
        fuente.setFontHeight(14);
        estilo.setFont(fuente);

        for(Producto producto : listaProductos) {
            Row fila = hoja.createRow(numeroFilas ++);

            Cell celda = fila.createCell(0);
            celda.setCellValue(producto.getId());
            hoja.autoSizeColumn(0);
            celda.setCellStyle(estilo);

            celda = fila.createCell(1);
            celda.setCellValue(producto.getNombre());
            hoja.autoSizeColumn(1);
            celda.setCellStyle(estilo);

            celda = fila.createCell(2);
            celda.setCellValue(producto.getPrecio());
            hoja.autoSizeColumn(2);
            celda.setCellStyle(estilo);

//            celda = fila.createCell(4);
//            celda.setCellValue(empleado.getFecha().toString());
//            hoja.autoSizeColumn(4);
//            celda.setCellStyle(estilo);

        }
    }

    public void exportar(HttpServletResponse response) throws IOException {
        escribirCabeceraDeTabla();
        escribirDatosDeLaTabla();

        ServletOutputStream outPutStream = response.getOutputStream();
        libro.write(outPutStream);

        libro.close();
        outPutStream.close();
    }
}
