package controller;

import entities.Categoria;
import entities.Producto;
import entities.ProductoProveedor;
import entities.ProductoProveedorPK;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import sessions.CategoriaFacade;
import sessions.ProductoProveedorFacade;
import sessions.ProveedorFacade;
import sessions.SesionActual;

@WebServlet(name = "ProductoServlet", loadOnStartup = 1, urlPatterns = {
    "/ListarProductos", "/AgregarProducto", "/EditarProducto", "/EliminarProducto",
    "/ProveedoresProducto", "/AgregarProveedorProducto", "/EliminarProveedorProducto"})
public class ProductoServlet extends BaseControlador {

    private static final int LARGO_CODIGO = 20;
    private static final int LARGO_NOMBRE_PRODUCTO = 60;
    private static final int LARGO_DESCRIPCION = 200;
    private static final int LARGO_UNIDAD = 15;
    private static final int LARGO_CODIGO_PROVEEDOR = 30;

    @EJB
    private CategoriaFacade categoriaF;
    @EJB
    private ProveedorFacade proveedorF;
    @EJB
    private ProductoProveedorFacade productoProveedorF;

    @Override
    public void init() {
        getServletContext().setAttribute("unidades",
                List.of("unidad", "caja", "pack", "kg", "litro", "metro"));
    }

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        String url = "/WEB-INF/views/ListarProductos.jsp";
        boolean post = "POST".equals(request.getMethod());
        Integer idNegocio = SesionActual.idNegocioActivo(request);

        switch (path) {
            case "/ListarProductos":
                request.setAttribute("productos", productoF.buscar(
                        request.getParameter("texto"),
                        entero(request, "categoria"),
                        request.getParameter("estado"),
                        request.getParameter("orden"),
                        idNegocio));
                request.setAttribute("categorias", categoriaF.findOrdenadas(idNegocio));
                url = "/WEB-INF/views/ListarProductos.jsp";
                break;

            case "/AgregarProducto":
                if (post) {
                    List<String> errores = validarProducto(request, null, idNegocio);
                    if (errores.isEmpty()) {
                        Producto nuevo = new Producto();
                        copiarProducto(request, nuevo, idNegocio);
                        nuevo.setNegocioIdnegocio(SesionActual.negocioActivo(request));
                        nuevo.setPerecedero(request.getParameter("perecedero") != null);
                        nuevo.setStock(nuevo.getPerecedero() ? 0 : entero(request, "stock"));
                        productoF.create(nuevo);
                        response.sendRedirect(request.getContextPath() + "/ListarProductos");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                request.setAttribute("categorias", categoriaF.findOrdenadas(idNegocio));
                url = "/WEB-INF/views/FormProducto.jsp";
                break;

            case "/EditarProducto": {
                Integer id = entero(request, "id");
                Producto producto = id == null ? null : productoF.findEnNegocio(id, idNegocio);
                if (producto == null) {
                    response.sendRedirect(request.getContextPath() + "/ListarProductos?error="
                            + codificar("El producto que querés editar ya no existe."));
                    return;
                }
                if (post) {
                    List<String> errores = validarProducto(request, id, idNegocio);
                    if (errores.isEmpty()) {
                        Producto datos = new Producto(id);
                        copiarProducto(request, datos, idNegocio);
                        productoF.editarDatos(datos);
                        response.sendRedirect(request.getContextPath() + "/ListarProductos");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                request.setAttribute("producto", producto);
                request.setAttribute("categorias", categoriaF.findOrdenadas(idNegocio));
                url = "/WEB-INF/views/FormProducto.jsp";
                break;
            }

            case "/EliminarProducto": {
                String error = "";
                Integer id = entero(request, "id");
                if (post && id != null) {
                    Producto producto = productoF.findEnNegocio(id, idNegocio);
                    if (producto == null) {
                        error = "?error=" + codificar("Ese producto ya no existe.");
                    } else if (productoF.hasRelations(id)) {
                        error = "?error=" + codificar("No se puede eliminar el producto: "
                                + "tiene movimientos o lotes registrados. "
                                + "El historial de stock no se borra.");
                    } else {
                        productoF.eliminar(producto);
                    }
                }
                response.sendRedirect(request.getContextPath() + "/ListarProductos" + error);
                return;
            }

            case "/ProveedoresProducto": {
                Integer id = entero(request, "id");
                Producto producto = id == null ? null : productoF.findEnNegocio(id, idNegocio);
                if (producto == null) {
                    response.sendRedirect(request.getContextPath() + "/ListarProductos?error="
                            + codificar("Ese producto ya no existe."));
                    return;
                }
                request.setAttribute("producto", producto);
                request.setAttribute("asignados", productoProveedorF.findByProducto(id));
                request.setAttribute("proveedores", proveedorF.findOrdenados(idNegocio));
                url = "/WEB-INF/views/ProveedoresProducto.jsp";
                break;
            }

            case "/AgregarProveedorProducto": {
                Integer idProducto = entero(request, "idProducto");
                Producto producto = idProducto == null ? null
                        : productoF.findEnNegocio(idProducto, idNegocio);
                if (producto == null) {
                    response.sendRedirect(request.getContextPath() + "/ListarProductos");
                    return;
                }
                if (post) {
                    List<String> errores = validarVinculo(request, idProducto, idNegocio);
                    if (errores.isEmpty()) {
                        ProductoProveedor vinculo = new ProductoProveedor(
                                new ProductoProveedorPK(idProducto,
                                        entero(request, "idProveedor")));
                        vinculo.setPrecioCompra(new BigDecimal(
                                request.getParameter("precioCompra").trim()));
                        vinculo.setDiasEntrega(entero(request, "diasEntrega"));
                        vinculo.setCodigoProveedor(parametroTexto(request, "codigoProveedor"));
                        productoProveedorF.create(vinculo);
                        response.sendRedirect(request.getContextPath()
                                + "/ProveedoresProducto?id=" + idProducto);
                        return;
                    }
                    response.sendRedirect(request.getContextPath()
                            + "/ProveedoresProducto?id=" + idProducto
                            + "&error=" + codificar(String.join(" ", errores)));
                    return;
                }
                response.sendRedirect(request.getContextPath()
                        + "/ProveedoresProducto?id=" + idProducto);
                return;
            }

            case "/EliminarProveedorProducto": {
                Integer idProducto = entero(request, "idProducto");
                Integer idProveedor = entero(request, "idProveedor");
                Producto producto = idProducto == null ? null
                        : productoF.findEnNegocio(idProducto, idNegocio);
                if (post && producto != null && idProveedor != null) {
                    ProductoProveedor vinculo = productoProveedorF.find(
                            new ProductoProveedorPK(idProducto, idProveedor));
                    if (vinculo != null) {
                        productoProveedorF.remove(vinculo);
                    }
                }
                response.sendRedirect(request.getContextPath()
                        + "/ProveedoresProducto?id=" + idProducto);
                return;
            }

            default:
                break;
        }
        despachar(request, response, url);
    }

    private List<String> validarProducto(HttpServletRequest request, Integer idActual,
            Integer idNegocio) {
        List<String> errores = new ArrayList<>();

        String codigo = parametroTexto(request, "codigo");
        if (codigo == null) {
            errores.add("El código es obligatorio.");
        } else if (codigo.length() > LARGO_CODIGO) {
            errores.add("El código no puede tener más de " + LARGO_CODIGO + " caracteres.");
        } else if (productoF.existeCodigo(codigo, idActual, idNegocio)) {
            errores.add("Ya hay otro producto con el código " + codigo + ".");
        }

        String nombre = parametroTexto(request, "nombre");
        if (nombre == null) {
            errores.add("El nombre es obligatorio.");
        } else if (nombre.length() > LARGO_NOMBRE_PRODUCTO) {
            errores.add("El nombre no puede tener más de " + LARGO_NOMBRE_PRODUCTO
                    + " caracteres.");
        }

        String descripcion = parametroTexto(request, "descripcion");
        if (descripcion != null && descripcion.length() > LARGO_DESCRIPCION) {
            errores.add("La descripción no puede tener más de " + LARGO_DESCRIPCION
                    + " caracteres.");
        }

        String unidad = parametroTexto(request, "unidad");
        if (unidad == null) {
            errores.add("Hay que elegir una unidad.");
        } else if (unidad.length() > LARGO_UNIDAD) {
            errores.add("La unidad no puede tener más de " + LARGO_UNIDAD + " caracteres.");
        }

        if (idActual == null) {
            validarEntero(request, "stock", "El stock inicial", errores);
        }
        validarEntero(request, "stockMinimo", "El stock mínimo", errores);
        validarDecimal(request, "precio", "El precio", errores);

        Integer idCategoria = entero(request, "categoria");
        Categoria categoria = idCategoria == null ? null
                : categoriaF.findEnNegocio(idCategoria, idNegocio);
        if (categoria == null) {
            errores.add("Hay que elegir una categoría de la lista.");
        }
        return errores;
    }

    private List<String> validarVinculo(HttpServletRequest request, Integer idProducto,
            Integer idNegocio) {
        List<String> errores = new ArrayList<>();

        Integer idProveedor = entero(request, "idProveedor");
        if (idProveedor == null || proveedorF.findEnNegocio(idProveedor, idNegocio) == null) {
            errores.add("Hay que elegir un proveedor de la lista.");
        } else if (productoProveedorF.existe(idProducto, idProveedor)) {
            errores.add("Ese proveedor ya está asignado a este producto: "
                    + "quitalo primero si querés cambiarle el precio.");
        }

        validarDecimal(request, "precioCompra", "El precio de compra", errores);

        String dias = request.getParameter("diasEntrega");
        if (dias != null && !dias.trim().isEmpty()) {
            validarEntero(request, "diasEntrega", "Los días de entrega", errores);
        }

        String codigo = parametroTexto(request, "codigoProveedor");
        if (codigo != null && codigo.length() > LARGO_CODIGO_PROVEEDOR) {
            errores.add("El código del proveedor no puede tener más de " + LARGO_CODIGO_PROVEEDOR
                    + " caracteres.");
        }
        return errores;
    }

    private void copiarProducto(HttpServletRequest request, Producto producto, Integer idNegocio) {
        producto.setCodigo(parametroTexto(request, "codigo"));
        producto.setNombre(parametroTexto(request, "nombre"));
        producto.setDescripcion(parametroTexto(request, "descripcion"));
        producto.setUnidad(parametroTexto(request, "unidad"));
        producto.setStockMinimo(entero(request, "stockMinimo"));
        producto.setPrecio(new BigDecimal(request.getParameter("precio").trim()));
        producto.setCategoriaIdcategoria(
                categoriaF.findEnNegocio(entero(request, "categoria"), idNegocio));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }
}
