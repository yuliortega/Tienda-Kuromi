package edu.poo.vista.categoria;

import edu.poo.modelo.Categoria;
import edu.poo.recurso.dominio.Ruta;
import edu.poo.recurso.utilidad.Fondo;
import edu.poo.recurso.utilidad.Marco;
import edu.poo.recurso.utilidad.Mensaje;
import edu.poo.recurso.dominio.Configuracion;
import edu.poo.recurso.dominio.Contenedor;
import edu.poo.controlador.varios.ControladorImagen;
import edu.poo.controlador.varios.ControladorFormulario;
import edu.poo.controlador.categoria.ControladorCategoEditar;
import edu.poo.persistencia.DAOProducto;
import java.io.File;
import javafx.geometry.Insets; // Importar Insets para el padding
import javafx.geometry.Pos;
import javafx.scene.SubScene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class VistaCategoEditar extends SubScene {
    private final GridPane miGrilla;
    private final StackPane miFormulario;
    // Elimina estas variables fijas, las dimensiones del marco serán dinámicas
    // private final double anchoMarco;
    // private final double altoMarco;
    private String rutaSeleccionada;
    private ImageView imagenPreview;

    private TextField cajaNombre;
    private TextField cajaImagen;
    private ComboBox<String> comboEstado;

    private final Categoria objCategoria;
    private final int posicion;
    private final BorderPane panelPrincipal;
    private Pane panelCuerpo;

    public VistaCategoEditar(BorderPane princ, Pane pane, double anchoFormulario, double altoFormulario,
                             Categoria categoriaExterna, int posicionArchivo) {
        super(new StackPane(), anchoFormulario, altoFormulario);
        this.panelPrincipal = princ;
        this.panelCuerpo = pane;
        this.objCategoria = categoriaExterna;
        this.posicion = posicionArchivo;

        miFormulario = (StackPane) getRoot();
        miFormulario.setBackground(Fondo.asignarAleatorio(Configuracion.FONDOS));
        miFormulario.setAlignment(Pos.CENTER); // Alinea todo el contenido al centro

        // Ya no necesitamos anchoMarco y altoMarco fijos aquí
        // anchoMarco = anchoFormulario * 0.7;
        // altoMarco = altoFormulario * 0.8;
        miGrilla = new GridPane();

        crearMarco();
        crearFormulario();
        // Carga la imagen inicial después de que la interfaz esté lista
        actualizarPreviewImagen(Ruta.RUTA_FOTOS + Configuracion.SEPARADOR_CARPETA + objCategoria.getNomImgPubCategoria());
    }
     cargarImgExistente();

    public StackPane getMiFormulario() {
        return miFormulario;
    }
    
    private void crearMarco() {
        Rectangle marco = Marco.crear(100, 100, 
                Configuracion.DEGRADE_ARREGLO, Configuracion.DEGRADE_BORDE); 
        marco.widthProperty().bind(miFormulario.widthProperty().multiply(0.85)); 
        marco.heightProperty().bind(miFormulario.heightProperty().multiply(0.90)); 
        
        marco.setOpacity(0.8);
        
        StackPane.setAlignment(marco, Pos.CENTER); // Asegura que el marco esté centrado
        miFormulario.getChildren().add(marco);
    }

    private FileChooser crearSelectorDeImagen() {
        File rutaInicial = new File(Ruta.RUTA_USUARIO);
        if (!rutaInicial.exists()) rutaInicial = new File(Ruta.RUTA_PROYECTO);
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar imagen de categoría");
        selector.setInitialDirectory(rutaInicial);
        selector.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes PNG", "*.png"),
                new FileChooser.ExtensionFilter("Fotos JPG", "*.jpg")
        );
        return selector;
    }

    private void crearFormulario() {
        miGrilla.setAlignment(Pos.CENTER);
        miGrilla.setHgap(10);
        miGrilla.setVgap(10);
        miGrilla.setPadding(new Insets(20)); // Añade padding al GridPane para que el contenido no toque el marco

        // Las restricciones de columna se vincularán al ancho del GridPane
        ColumnConstraints col1 = new ColumnConstraints();
        col1.percentWidthProperty().bind(miGrilla.widthProperty().multiply(0.3 / (0.3 + 0.7))); // 30% del ancho del grid
        col1.setHgrow(Priority.ALWAYS); // Permite crecimiento horizontal
        ColumnConstraints col2 = new ColumnConstraints();
        col2.percentWidthProperty().bind(miGrilla.widthProperty().multiply(0.7 / (0.3 + 0.7))); // 70% del ancho del grid
        col2.setHgrow(Priority.ALWAYS); // Permite crecimiento horizontal
        miGrilla.getColumnConstraints().addAll(col1, col2);

        // Bind the GridPane's dimensions to the miFormulario's dimensions
        // Adjust the multipliers to ensure the form doesn't cover the entire frame
        miGrilla.maxWidthProperty().bind(miFormulario.widthProperty().multiply(0.75)); // 75% del ancho del formulario
        miGrilla.maxHeightProperty().bind(miFormulario.heightProperty().multiply(0.80)); // 80% del alto del formulario

        Text titulo = new Text("Editar Categoría");
        titulo.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        titulo.setFill(Color.web("#E6E6E6"));
        GridPane.setHalignment(titulo, javafx.geometry.HPos.CENTER); // Centra el título en las columnas combinadas
        miGrilla.add(titulo, 0, 0, 2, 1);

        // Nombre
        Label lblNombre = new Label("Nombre de Categoría:");
        lblNombre.setTextFill(Color.web("#000000")); // Color para el texto de la etiqueta
        miGrilla.add(lblNombre, 0, 1);
        cajaNombre = new TextField(objCategoria.getNombreCategoria());
        cajaNombre.setMaxWidth(Double.MAX_VALUE); // Permite que el TextField se expanda
        ControladorFormulario.cantidadCaracteres(cajaNombre, 30);
        miGrilla.add(cajaNombre, 1, 1);

        // Imagen
        Label lblImg = new Label("Imagen:");
        lblImg.setTextFill(Color.web("#000000")); // Color para el texto de la etiqueta
        miGrilla.add(lblImg, 0, 2);
        cajaImagen = new TextField(objCategoria.getNomImgPubCategoria());
        cajaImagen.setDisable(true);
        cajaImagen.setMaxWidth(Double.MAX_VALUE); // Permite que el TextField se expanda
        Button btnImg = new Button("Seleccionar");
        FileChooser selector = crearSelectorDeImagen();
        btnImg.setOnAction(e -> {
            rutaSeleccionada = ControladorImagen.seleccionarImagen(cajaImagen, selector);
            if (rutaSeleccionada.isEmpty()) {
                Mensaje.modal(Alert.AlertType.WARNING, null, "Advertencia", "No se seleccionó una imagen.");
            } else {
                actualizarPreviewImagen(rutaSeleccionada);
            }
        });
        HBox hbImg = new HBox(5, cajaImagen, btnImg);
        hbImg.setAlignment(Pos.CENTER_LEFT); // Alinea los elementos a la izquierda en el HBox
        HBox.setHgrow(cajaImagen, Priority.ALWAYS);
        miGrilla.add(hbImg, 1, 2);
        
        imagenPreview = new ImageView();
        imagenPreview.setFitWidth(100);
        imagenPreview.setFitHeight(100);
        GridPane.setHalignment(imagenPreview, javafx.geometry.HPos.CENTER); // Centra la imagen
        miGrilla.add(imagenPreview, 0, 3, 2, 1); // Ocupa ambas columnas para centrado

        // Estado
        Label lblEstado = new Label("Estado:");
        lblEstado.setTextFill(Color.web("#000000")); // Color para el texto de la etiqueta
        miGrilla.add(lblEstado, 0, 4); // Ajustado a la fila 4
        comboEstado = new ComboBox<>();
        comboEstado.getItems().addAll("Activo", "Inactivo");
        comboEstado.setValue(objCategoria.isEstadoCategoria() ? "Activo" : "Inactivo");
        comboEstado.setMaxWidth(Double.MAX_VALUE); // Permite que el ComboBox se expanda
        miGrilla.add(comboEstado, 1, 4); // Ajustado a la fila 4
        
        // Botones
        HBox panelBotones = new HBox(10); // 10 pixels de espacio entre botones
        panelBotones.setAlignment(Pos.CENTER);
        
        Button btnActualizar = new Button("Actualizar");
        btnActualizar.setPrefWidth(120);
        btnActualizar.setOnAction(e -> manejarActualizar());
        
        Button btnRegresar = new Button("Regresar");
        btnRegresar.setPrefWidth(120);
        btnRegresar.setOnAction(e -> {
            panelCuerpo = new VistaCategoAdmin(panelPrincipal, panelCuerpo, 
                Configuracion.ANCHO_APP, Contenedor.ALTO_CUERPO.getValor()).getMiFormulario();
            panelPrincipal.setCenter(panelCuerpo);
        });
        
        panelBotones.getChildren().addAll(btnActualizar, btnRegresar);
        GridPane.setHalignment(panelBotones, javafx.geometry.HPos.CENTER); // Centra los botones en la grilla
        miGrilla.add(panelBotones, 0, 5, 2, 1); // Ocupa ambas columnas para centrado, ajustado a fila 5

        miFormulario.getChildren().add(miGrilla);
    }

    private void actualizarPreviewImagen(String rutaImagen) {
        File archivoImagen = new File(rutaImagen);
        if (archivoImagen.exists()) {
            imagenPreview.setImage(new Image(archivoImagen.toURI().toString()));
        } else {
            // Si la imagen no existe, puedes establecer una imagen por defecto o dejarla en null
            imagenPreview.setImage(null); 
            // Opcional: Cargar una imagen placeholder si la original no se encuentra
            // try {
            //     imagenPreview.setImage(new Image(getClass().getResourceAsStream("/ruta/a/placeholder.png")));
            // } catch (Exception e) {
            //     System.err.println("No se pudo cargar la imagen placeholder: " + e.getMessage());
            // }
        }
    }

    private void manejarActualizar() {
        if (validarFormulario()) {
            String nombre = cajaNombre.getText();
            boolean nuevoEstado = "Activo".equals(comboEstado.getValue());
            String nomImg = cajaImagen.getText();
            Categoria catEditada = new Categoria(objCategoria.getCodCategoria(), nombre, nuevoEstado);
            catEditada.setNomImgPubCategoria(nomImg);
            // conservar nombre oculto si existe
            catEditada.setNomImgOcuCategoria(objCategoria.getNomImgOcuCategoria());
            boolean ok = ControladorCategoEditar.actualizar(posicion, catEditada, rutaSeleccionada);
            if (ok) {
                Mensaje.modal(Alert.AlertType.INFORMATION, null, "Éxito", "Categoría actualizada.");
                
                // Volver a la vista de administración de categorías
                panelCuerpo = new VistaCategoAdmin(panelPrincipal, panelCuerpo, 
                    Configuracion.ANCHO_APP, Contenedor.ALTO_CUERPO.getValor()).getMiFormulario();
                panelPrincipal.setCenter(panelCuerpo);
            } else {
                Mensaje.modal(Alert.AlertType.ERROR, null, "Error", "No se pudo actualizar la categoría.");
            }
        }
    }

    private boolean validarFormulario() {
        if (cajaNombre.getText().isBlank()) {
            cajaNombre.requestFocus();
            Mensaje.modal(Alert.AlertType.WARNING, null, "Advertencia", "El nombre es obligatorio.");
            return false;
        }
        if (comboEstado.getValue() == null) {
            comboEstado.requestFocus();
            Mensaje.modal(Alert.AlertType.WARNING, null, "Advertencia", "Debe seleccionar un estado.");
            return false;
        }
        return true;
    }

    if (objCategoria.getNomImgPubCategoria() != null && !objCategoria.getNomImgPubCategoria().isEmpty()) {

            String nombreOcuLimpio = objCategoria.getNomImgOcuCategoria();
            if (nombreOcuLimpio != null) {
                nombreOcuLimpio = nombreOcuLimpio.replace("@", "").trim();
            }

            String rutaImagen = Ruta.RUTA_FOTOS + Configuracion.SEPARADOR_CARPETA + nombreOcuLimpio;
            File archivo = new File(rutaImagen);

            if (archivo.exists()) {
                Image imagen = new Image(archivo.toURI().toString());
                imagenPreview.setImage(imagen);
                cajaImagen.setText(objCategoria.getNomImgPubCategoria());
                rutaSeleccionada = "";
            } else {
                System.err.println("No se encontró la imagen : " + rutaImagen);
                imagenPreview.setImage(null);
            }
        }
    }
}
