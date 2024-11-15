package com.kalek.crud.controller;



import com.kalek.crud.dto.Mensaje;
import com.kalek.crud.dto.ProductoDTO;
import com.kalek.crud.models.Producto;
import com.kalek.crud.repository.ProductoRepository;
import com.kalek.crud.service.ExportExProductoService;
import com.kalek.crud.service.ExportProductoService;
import com.kalek.crud.service.ProductoService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.lowagie.text.DocumentException;

@RestController
@RequestMapping("/producto")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExportProductoService exportService;
    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/listar")
    public List<Producto> listaDeProductos(){
        return productoService.listarProductos();
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<Producto> detalleProducto(@PathVariable Long id){
        if(!productoService.existeElId(id)){
            return new ResponseEntity(new Mensaje("No existe el id"),HttpStatus.NOT_FOUND);
        }
        Optional<Producto> producto=productoService.buscarPorId(id);
       return new ResponseEntity(producto,HttpStatus.OK);
    }

    @GetMapping("lista/{nombre}")
    public ResponseEntity<Producto> obtenerNombre(@PathVariable String nombre){
        if(!productoService.existeNombre(nombre)){
            return new ResponseEntity(new Mensaje("No existe el nombre"),HttpStatus.NOT_FOUND);
        }
        Producto producto =productoService.buscarPorNombre(nombre).get();
        return new ResponseEntity(producto,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody ProductoDTO productoDTO){

        if(productoDTO.getPrecio() < 0){
            return new ResponseEntity(new Mensaje("El producto debe sr mayor que 0"),HttpStatus.BAD_REQUEST);
        }
        if(productoService.existeNombre(productoDTO.getNombre())){
            return new ResponseEntity(new Mensaje("Ya existe el producto con ese nombre"),HttpStatus.BAD_REQUEST);
        }
        //Dto a entity
        Producto productoReq = modelMapper.map(productoDTO, Producto.class);
        Producto producto=productoService.guardar(productoReq);
        //enttity a deto
        ProductoDTO productoRes=modelMapper.map(producto,ProductoDTO.class);
        return new ResponseEntity(productoRes,HttpStatus.CREATED);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<?> editarProducto(@PathVariable Long id, @RequestBody ProductoDTO productoDTO){
        if(!productoService.existeElId(id)){
            return new ResponseEntity(new Mensaje("No existe el id"),HttpStatus.NOT_FOUND);
        }
        if(productoDTO.getPrecio()==null || productoDTO.getPrecio()<0){
            return new ResponseEntity(new Mensaje("El producto debe sr mayor que 0"),HttpStatus.BAD_REQUEST);
        }
        if(productoService.existeNombre(productoDTO.getNombre()) && productoService.buscarPorNombre(productoDTO.getNombre())
                .get().getId() != id){
            return new ResponseEntity(new Mensaje("Ya existe el producto con ese nombre"),HttpStatus.BAD_REQUEST);
        }
        Producto producto=productoService.buscarPorId(id).get();
        producto.setNombre(productoDTO.getNombre());
        producto.setPrecio(productoDTO.getPrecio());
        productoService.guardar(producto);
        return new ResponseEntity(new Mensaje("producto actualizado"),HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        if(!productoService.existeElId(id)){
            return new ResponseEntity(new Mensaje("No existe el id"),HttpStatus.NOT_FOUND);
        }
        productoService.eliminar(id);
        return new ResponseEntity(new Mensaje("producto eliminado"),HttpStatus.OK);
    }

    @GetMapping("/exportarPDF")
    public void exportarListadoDeProductosEnPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Productos_" + fechaActual + ".pdf";

        response.setHeader(cabecera, valor);

        List<Producto> productos = productoService.listarProductos();

        ExportProductoService exporter = new ExportProductoService(productos);
        exporter.exportar(response);
    }

    @GetMapping("/exportarExcel")
    public void exportarListadoDeEmpleadosEnExcel(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Productos_" + fechaActual + "_.xlsx";

        response.setHeader(cabecera, valor);

        List<Producto> productos = productoService.listarProductos();

        ExportExProductoService exporter = new ExportExProductoService(productos);
        exporter.exportar(response);
    }
}
