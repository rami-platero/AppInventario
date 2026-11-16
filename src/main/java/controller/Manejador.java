package controller;

import entities.Categoria;
import entities.Lote;
import entities.Producto;
import entities.ProductoProveedor;
import entities.ProductoProveedorPK;
import entities.Proveedor;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sessions.CategoriaFacade;
import sessions.LoteFacade;
import sessions.MovimientoInvalidoException;
import sessions.MovimientoStockFacade;
import sessions.ProductoFacade;
import sessions.ProductoProveedorFacade;
import sessions.ProveedorFacade;

@WebServlet(name = "Manejador", loadOnStartup = 1,
        urlPatterns = {"/Panel",
            "/ListarProductos", "/AgregarProducto", "/EditarProducto", "/EliminarProducto",
            "/ProveedoresProducto", "/AgregarProveedorProducto", "/EliminarProveedorProducto",
            "/ListarCategorias", "/AgregarCategoria", "/EditarCategoria", "/EliminarCategoria",
            "/ListarProveedores", "/AgregarProveedor", "/EditarProveedor", "/EliminarProveedor",
            "/ListarMovimientos", "/RegistrarMovimiento",
            "/Vencimientos", "/DescartarLote",
            "/Reponer", "/MasVendidos", "/ComprasPorProveedor", "/SinProveedor", "/ProveedoresTop"})
public class Manejador extends HttpServlet {

    private static final int LARGO_CODIGO = 20;
    private static final int LARGO_NOMBRE_PRODUCTO = 60;
    private static final int LARGO_DESCRIPCION = 200;
    private static final int LARGO_UNIDAD = 15;
    private static final int LARGO_NOMBRE = 45;
    private static final int LARGO_TELEFONO = 20;
    private static final int LARGO_EMAIL = 60;
    private static final int LARGO_CODIGO_LOTE = 30;
    private static final int LARGO_MOTIVO = 200;

    @EJB
    private CategoriaFacade categoriaF;
    @EJB
    private ProveedorFacade proveedorF;
    @EJB
    private ProductoFacade productoF;
    @EJB
    private ProductoProveedorFacade productoProveedorF;
    @EJB
    private MovimientoStockFacade movimientoF;
    @EJB
    private LoteFacade loteF;

    @Override
    public void init() throws ServletException {
        getServletContext().setAttribute("unidades",
                List.of("unidad", "caja", "pack", "kg", "litro", "metro"));
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        String url = "/WEB-INF/views/Panel.jsp";
        boolean post = "POST".equals(request.getMethod());

        switch (path) {
            case "/Panel":
                request.setAttribute("cantidadProductos", productoF.count());
                request.setAttribute("totalUnidades", productoF.totalUnidades());
                request.setAttribute("valorInventario", productoF.valorInventario());
                request.setAttribute("cantidadCategorias", categoriaF.count());
                request.setAttribute("cantidadProveedores", proveedorF.count());
                request.setAttribute("reponer", productoF.findBajoMinimo());
                request.setAttribute("movimientos", movimientoF.findUltimos(8));
                url = "/WEB-INF/views/Panel.jsp";
                break;

            case "/ListarProductos":
                request.setAttribute("productos", productoF.buscar(
                        request.getParameter("texto"),
                        entero(request, "categoria"),
                        request.getParameter("estado"),
                        request.getParameter("orden")));
                request.setAttribute("categorias", categoriaF.findOrdenadas());
                url = "/WEB-INF/views/ListarProductos.jsp";
                break;

            case "/AgregarProducto":
                if (post) {
                    List<String> errores = validarProducto(request, null);
                    if (errores.isEmpty()) {
                        Producto nuevo = new Producto();
                        copiarProducto(request, nuevo);
                        nuevo.setPerecedero(request.getParameter("perecedero") != null);
                        nuevo.setStock(nuevo.getPerecedero() ? 0 : entero(request, "stock"));
                        productoF.create(nuevo);
                        response.sendRedirect(request.getContextPath() + "/ListarProductos");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                request.setAttribute("categorias", categoriaF.findOrdenadas());
                url = "/WEB-INF/views/FormProducto.jsp";
                break;

            case "/EditarProducto": {
                Integer id = entero(request, "id");
                Producto producto = id == null ? null : productoF.find(id);
                if (producto == null) {
                    response.sendRedirect(request.getContextPath() + "/ListarProductos?error="
                            + codificar("El producto que querés editar ya no existe."));
                    return;
                }
                if (post) {
                    List<String> errores = validarProducto(request, id);
                    if (errores.isEmpty()) {
                        Producto datos = new Producto(id);
                        copiarProducto(request, datos);
                        productoF.editarDatos(datos);
                        response.sendRedirect(request.getContextPath() + "/ListarProductos");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                request.setAttribute("producto", producto);
                request.setAttribute("categorias", categoriaF.findOrdenadas());
                url = "/WEB-INF/views/FormProducto.jsp";
                break;
            }

            case "/EliminarProducto": {
                String error = "";
                Integer id = entero(request, "id");
                if (post && id != null) {
                    if (productoF.hasRelations(id)) {
                        error = "?error=" + codificar("No se puede eliminar el producto: "
                                + "tiene movimientos o lotes registrados. "
                                + "El historial de stock no se borra.");
                    } else {
                        Producto producto = productoF.find(id);
                        if (producto != null) {
                            productoF.eliminar(producto);
                        }
                    }
                }
                response.sendRedirect(request.getContextPath() + "/ListarProductos" + error);
                return;
            }

            case "/ProveedoresProducto": {
                Integer id = entero(request, "id");
                Producto producto = id == null ? null : productoF.find(id);
                if (producto == null) {
                    response.sendRedirect(request.getContextPath() + "/ListarProductos?error="
                            + codificar("Ese producto ya no existe."));
                    return;
                }
                request.setAttribute("producto", producto);
                request.setAttribute("asignados", productoProveedorF.findByProducto(id));
                request.setAttribute("proveedores", proveedorF.findOrdenados());
                url = "/WEB-INF/views/ProveedoresProducto.jsp";
                break;
            }

            case "/AgregarProveedorProducto": {
                Integer idProducto = entero(request, "idProducto");
                if (idProducto == null) {
                    response.sendRedirect(request.getContextPath() + "/ListarProductos");
                    return;
                }
                if (post) {
                    List<String> errores = validarVinculo(request, idProducto);
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
                if (post && idProducto != null && idProveedor != null) {
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

            case "/ListarCategorias": {
                Map<Integer, Long> alertas = new HashMap<>();
                for (Object[] fila : categoriaF.countAlertasPorCategoria()) {
                    alertas.put((Integer) fila[0], (Long) fila[1]);
                }
                request.setAttribute("categorias", categoriaF.findConCantidadDeProductos());
                request.setAttribute("alertas", alertas);
                url = "/WEB-INF/views/ListarCategorias.jsp";
                break;
            }

            case "/AgregarCategoria":
                if (post) {
                    List<String> errores = validarCategoria(request, null);
                    if (errores.isEmpty()) {
                        Categoria nueva = new Categoria();
                        nueva.setNombre(parametroTexto(request, "nombre"));
                        categoriaF.create(nueva);
                        response.sendRedirect(request.getContextPath() + "/ListarCategorias");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                url = "/WEB-INF/views/FormCategoria.jsp";
                break;

            case "/EditarCategoria": {
                Integer id = entero(request, "id");
                Categoria categoria = id == null ? null : categoriaF.find(id);
                if (categoria == null) {
                    response.sendRedirect(request.getContextPath() + "/ListarCategorias?error="
                            + codificar("Esa categoría ya no existe."));
                    return;
                }
                if (post) {
                    List<String> errores = validarCategoria(request, id);
                    if (errores.isEmpty()) {
                        categoria.setNombre(parametroTexto(request, "nombre"));
                        categoriaF.edit(categoria);
                        response.sendRedirect(request.getContextPath() + "/ListarCategorias");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                request.setAttribute("categoria", categoria);
                url = "/WEB-INF/views/FormCategoria.jsp";
                break;
            }

            case "/EliminarCategoria": {
                String error = "";
                Integer id = entero(request, "id");
                if (post && id != null) {
                    if (categoriaF.hasRelations(id)) {
                        error = "?error=" + codificar("No se puede eliminar la categoría: "
                                + "tiene productos asociados.");
                    } else {
                        Categoria categoria = categoriaF.find(id);
                        if (categoria != null) {
                            categoriaF.remove(categoria);
                        }
                    }
                }
                response.sendRedirect(request.getContextPath() + "/ListarCategorias" + error);
                return;
            }

            case "/ListarProveedores":
                request.setAttribute("proveedores", proveedorF.findConContadores());
                url = "/WEB-INF/views/ListarProveedores.jsp";
                break;

            case "/AgregarProveedor":
                if (post) {
                    List<String> errores = validarProveedor(request, null);
                    if (errores.isEmpty()) {
                        Proveedor nuevo = new Proveedor();
                        copiarProveedor(request, nuevo);
                        proveedorF.create(nuevo);
                        response.sendRedirect(request.getContextPath() + "/ListarProveedores");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                url = "/WEB-INF/views/FormProveedor.jsp";
                break;

            case "/EditarProveedor": {
                Integer id = entero(request, "id");
                Proveedor proveedor = id == null ? null : proveedorF.find(id);
                if (proveedor == null) {
                    response.sendRedirect(request.getContextPath() + "/ListarProveedores?error="
                            + codificar("Ese proveedor ya no existe."));
                    return;
                }
                if (post) {
                    List<String> errores = validarProveedor(request, id);
                    if (errores.isEmpty()) {
                        copiarProveedor(request, proveedor);
                        proveedorF.edit(proveedor);
                        response.sendRedirect(request.getContextPath() + "/ListarProveedores");
                        return;
                    }
                    request.setAttribute("errores", errores);
                }
                request.setAttribute("proveedor", proveedor);
                url = "/WEB-INF/views/FormProveedor.jsp";
                break;
            }

            case "/EliminarProveedor": {
                String error = "";
                Integer id = entero(request, "id");
                if (post && id != null) {
                    if (proveedorF.hasRelations(id)) {
                        error = "?error=" + codificar("No se puede eliminar el proveedor: "
                                + "tiene productos asignados o compras registradas.");
                    } else {
                        Proveedor proveedor = proveedorF.find(id);
                        if (proveedor != null) {
                            proveedorF.remove(proveedor);
                        }
                    }
                }
                response.sendRedirect(request.getContextPath() + "/ListarProveedores" + error);
                return;
            }

            case "/ListarMovimientos":
                request.setAttribute("movimientos", movimientoF.buscar(
                        entero(request, "producto"), request.getParameter("tipo")));
                request.setAttribute("productos", productoF.findOrdenados());
                url = "/WEB-INF/views/ListarMovimientos.jsp";
                break;

            case "/RegistrarMovimiento":
                if (post) {
                    List<String> errores = validarMovimiento(request);
                    if (errores.isEmpty()) {
                        try {
                            movimientoF.registrar(
                                    entero(request, "producto"),
                                    request.getParameter("tipo"),
                                    entero(request, "cantidad"),
                                    entero(request, "proveedor"),
                                    parametroTexto(request, "motivo"),
                                    fecha(request, "vencimiento"),
                                    parametroTexto(request, "codigoLote"),
                                    entero(request, "lote"));
                            response.sendRedirect(request.getContextPath() + "/ListarMovimientos");
                            return;
                        } catch (MovimientoInvalidoException e) {
                            errores.add(e.getMessage());
                        }
                    }
                    request.setAttribute("errores", errores);
                }
                request.setAttribute("productos", productoF.findOrdenados());
                request.setAttribute("proveedores", proveedorF.findOrdenados());
                request.setAttribute("lotes", loteF.findConStock());
                url = "/WEB-INF/views/FormMovimiento.jsp";
                break;

            case "/Vencimientos":
                request.setAttribute("sugerencias",
                        loteF.analizarVencimientos(LoteFacade.DIAS_AVISO));
                request.setAttribute("unidadesPorVencer",
                        loteF.unidadesPorVencer(LoteFacade.DIAS_AVISO));
                request.setAttribute("diasAviso", LoteFacade.DIAS_AVISO);
                url = "/WEB-INF/views/Vencimientos.jsp";
                break;

            case "/DescartarLote": {
                String error = "";
                Integer id = entero(request, "id");
                if (post && id != null) {
                    Lote lote = loteF.find(id);
                    if (lote != null && lote.getCantidad() > 0) {
                        try {
                            movimientoF.registrar(
                                    lote.getProductoIdproducto().getIdproducto(),
                                    MovimientoStockFacade.SALIDA,
                                    lote.getCantidad(), null,
                                    "Merma: lote vencido o descartado",
                                    null, null, lote.getIdlote());
                        } catch (MovimientoInvalidoException e) {
                            error = "?error=" + codificar(e.getMessage());
                        }
                    }
                }
                response.sendRedirect(request.getContextPath() + "/Vencimientos" + error);
                return;
            }

            case "/Reponer":
                request.setAttribute("productos", productoF.findBajoMinimo());
                request.setAttribute("mejores", mejoresProveedores(productoF.findBajoMinimo()));
                url = "/WEB-INF/views/Reponer.jsp";
                break;

            case "/MasVendidos":
                request.setAttribute("filas", movimientoF.findMasVendidos(10));
                url = "/WEB-INF/views/MasVendidos.jsp";
                break;

            case "/ComprasPorProveedor":
                request.setAttribute("filas", movimientoF.findComprasPorProveedor());
                url = "/WEB-INF/views/ComprasPorProveedor.jsp";
                break;

            case "/SinProveedor":
                request.setAttribute("productos", productoF.findSinProveedor());
                url = "/WEB-INF/views/SinProveedor.jsp";
                break;

            case "/ProveedoresTop":
                request.setAttribute("proveedores", proveedorF.findConMasDeDosProductos());
                url = "/WEB-INF/views/ProveedoresTop.jsp";
                break;

            default:
                break;
        }
        cargarContadores(request);
        request.getRequestDispatcher(url).forward(request, response);
    }

    private List<String> validarProducto(HttpServletRequest request, Integer idActual) {
        List<String> errores = new ArrayList<>();

        String codigo = parametroTexto(request, "codigo");
        if (codigo == null) {
            errores.add("El código es obligatorio.");
        } else if (codigo.length() > LARGO_CODIGO) {
            errores.add("El código no puede tener más de " + LARGO_CODIGO + " caracteres.");
        } else if (productoF.existeCodigo(codigo, idActual)) {
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
        if (idCategoria == null || categoriaF.find(idCategoria) == null) {
            errores.add("Hay que elegir una categoría de la lista.");
        }
        return errores;
    }

    private List<String> validarCategoria(HttpServletRequest request, Integer idActual) {
        List<String> errores = new ArrayList<>();
        String nombre = parametroTexto(request, "nombre");
        if (nombre == null) {
            errores.add("El nombre es obligatorio.");
        } else if (nombre.length() > LARGO_NOMBRE) {
            errores.add("El nombre no puede tener más de " + LARGO_NOMBRE + " caracteres.");
        } else if (categoriaF.existeNombre(nombre, idActual)) {
            errores.add("Ya existe una categoría llamada " + nombre + ".");
        }
        return errores;
    }

    private List<String> validarProveedor(HttpServletRequest request, Integer idActual) {
        List<String> errores = new ArrayList<>();

        String nombre = parametroTexto(request, "nombre");
        if (nombre == null) {
            errores.add("El nombre es obligatorio.");
        } else if (nombre.length() > LARGO_NOMBRE) {
            errores.add("El nombre no puede tener más de " + LARGO_NOMBRE + " caracteres.");
        } else if (proveedorF.existeNombre(nombre, idActual)) {
            errores.add("Ya existe un proveedor llamado " + nombre + ".");
        }

        String telefono = parametroTexto(request, "telefono");
        if (telefono != null && telefono.length() > LARGO_TELEFONO) {
            errores.add("El teléfono no puede tener más de " + LARGO_TELEFONO + " caracteres.");
        }

        String email = parametroTexto(request, "email");
        if (email != null) {
            if (email.length() > LARGO_EMAIL) {
                errores.add("El email no puede tener más de " + LARGO_EMAIL + " caracteres.");
            } else if (!email.matches("[^@\\s]+@[^@\\s.]+\\.[^@\\s]+")) {
                errores.add("El email no tiene un formato válido.");
            }
        }
        return errores;
    }

    private List<String> validarVinculo(HttpServletRequest request, Integer idProducto) {
        List<String> errores = new ArrayList<>();

        Integer idProveedor = entero(request, "idProveedor");
        if (idProveedor == null || proveedorF.find(idProveedor) == null) {
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
        if (codigo != null && codigo.length() > LARGO_CODIGO_LOTE) {
            errores.add("El código del proveedor no puede tener más de " + LARGO_CODIGO_LOTE
                    + " caracteres.");
        }
        return errores;
    }

    private List<String> validarMovimiento(HttpServletRequest request) {
        List<String> errores = new ArrayList<>();

        Integer idProducto = entero(request, "producto");
        if (idProducto == null || productoF.find(idProducto) == null) {
            errores.add("Hay que elegir un producto de la lista.");
        }

        String tipo = request.getParameter("tipo");
        if (!MovimientoStockFacade.ENTRADA.equals(tipo)
                && !MovimientoStockFacade.SALIDA.equals(tipo)
                && !MovimientoStockFacade.AJUSTE.equals(tipo)) {
            errores.add("Hay que elegir el tipo de movimiento.");
        }

        validarEntero(request, "cantidad", "La cantidad", errores);

        String fecha = request.getParameter("vencimiento");
        if (fecha != null && !fecha.trim().isEmpty() && fecha(request, "vencimiento") == null) {
            errores.add("La fecha de vencimiento no es válida.");
        }

        String codigoLote = parametroTexto(request, "codigoLote");
        if (codigoLote != null && codigoLote.length() > LARGO_CODIGO_LOTE) {
            errores.add("El código del lote no puede tener más de " + LARGO_CODIGO_LOTE
                    + " caracteres.");
        }

        String motivo = parametroTexto(request, "motivo");
        if (motivo != null && motivo.length() > LARGO_MOTIVO) {
            errores.add("El motivo no puede tener más de " + LARGO_MOTIVO + " caracteres.");
        }
        return errores;
    }

    private void validarEntero(HttpServletRequest request, String campo, String rotulo,
            List<String> errores) {
        String valor = request.getParameter(campo);
        if (valor == null || valor.trim().isEmpty()) {
            errores.add(rotulo + " es obligatorio.");
            return;
        }
        Integer numero = entero(request, campo);
        if (numero == null) {
            errores.add(rotulo + " tiene que ser un número entero.");
        } else if (numero < 0) {
            errores.add(rotulo + " no puede ser negativo.");
        }
    }

    private void validarDecimal(HttpServletRequest request, String campo, String rotulo,
            List<String> errores) {
        String valor = request.getParameter(campo);
        if (valor == null || valor.trim().isEmpty()) {
            errores.add(rotulo + " es obligatorio.");
            return;
        }
        try {
            if (new BigDecimal(valor.trim()).signum() < 0) {
                errores.add(rotulo + " no puede ser negativo.");
            }
        } catch (NumberFormatException e) {
            errores.add(rotulo + " tiene que ser un número.");
        }
    }

    private void cargarContadores(HttpServletRequest request) {
        request.setAttribute("contadorReponer", productoF.countBajoMinimo());
        request.setAttribute("contadorAgotados", productoF.countAgotados());
        request.setAttribute("contadorPorVencer", loteF.countPorVencer(LoteFacade.DIAS_AVISO));
        request.setAttribute("contadorVencidos", loteF.countVencidos());
    }

    private Map<Integer, ProductoProveedor> mejoresProveedores(List<Producto> productos) {
        Map<Integer, ProductoProveedor> mejores = new HashMap<>();
        for (Producto producto : productos) {
            ProductoProveedor mejor = productoProveedorF.findMejorProveedor(producto.getIdproducto());
            if (mejor != null) {
                mejores.put(producto.getIdproducto(), mejor);
            }
        }
        return mejores;
    }

    private void copiarProducto(HttpServletRequest request, Producto producto) {
        producto.setCodigo(parametroTexto(request, "codigo"));
        producto.setNombre(parametroTexto(request, "nombre"));
        producto.setDescripcion(parametroTexto(request, "descripcion"));
        producto.setUnidad(parametroTexto(request, "unidad"));
        producto.setStockMinimo(entero(request, "stockMinimo"));
        producto.setPrecio(new BigDecimal(request.getParameter("precio").trim()));
        producto.setCategoriaIdcategoria(categoriaF.find(entero(request, "categoria")));
    }

    private void copiarProveedor(HttpServletRequest request, Proveedor proveedor) {
        proveedor.setNombre(parametroTexto(request, "nombre"));
        proveedor.setTelefono(parametroTexto(request, "telefono"));
        proveedor.setEmail(parametroTexto(request, "email"));
    }

    private Integer entero(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Date fecha(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return java.sql.Date.valueOf(valor.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String parametroTexto(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }

    private String codificar(String texto) {
        return URLEncoder.encode(texto, StandardCharsets.UTF_8);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
