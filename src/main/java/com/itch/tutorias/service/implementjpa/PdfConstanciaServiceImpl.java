package com.itch.tutorias.service.implementjpa;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.Tutor;
import com.itch.tutorias.model.Tutorado;
import com.itch.tutorias.service.IConstanciaService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfConstanciaServiceImpl implements IConstanciaService {

    @Override
    public byte[] generarConstanciaTutorado(Asignacion asignacion, Tutorado tutorado, double porcentaje) throws Exception {
        if (porcentaje < 80.0) {
            throw new Exception("El tutorado no cumple con el 80% mínimo de asistencia requerido para la liberación de créditos.");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setEncryption(null, null, PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128); // Read-only RNF-11
            
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            
            Paragraph header = new Paragraph("CONSTANCIA DE ACREDITACIÓN", titleFont);
            header.setAlignment(Paragraph.ALIGN_CENTER);
            header.setSpacingAfter(40);
            document.add(header);
            
            Paragraph p1 = new Paragraph("A quien corresponda:", textFont);
            p1.setSpacingAfter(20);
            document.add(p1);
            
            Paragraph body = new Paragraph();
            body.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            body.setFont(textFont);
            body.setSpacingAfter(30);
            body.add("El que suscribe, certifica que el/la estudiante ");
            body.add(new Chunk(tutorado.getUsuario().getNombreCompleto(), boldFont));
            body.add(" con número de control ");
            body.add(new Chunk(tutorado.getUsuario().getNumeroIdentificacion(), boldFont));
            body.add(", ha cumplido satisfactoriamente con las actividades del Programa Institucional de Tutorías, acreditando un ");
            body.add(new Chunk(porcentaje + "%", boldFont));
            body.add(" de asistencia durante el periodo ");
            body.add(new Chunk(asignacion.getPeriodo().getClave(), boldFont));
            body.add(", en el grupo ");
            body.add(new Chunk(asignacion.getGrupo().getNombre(), boldFont));
            body.add(" de la carrera de ");
            body.add(new Chunk(asignacion.getGrupo().getCarrera().getNombre(), boldFont));
            body.add(".");
            document.add(body);
            
            Paragraph body2 = new Paragraph("Se expide la presente constancia para los fines legales que al interesado(a) convengan, a los " + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'días del mes de' MM 'del año' yyyy")) + ".", textFont);
            body2.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            body2.setSpacingAfter(60);
            document.add(body2);
            
            Paragraph signature = new Paragraph("___________________________________\nDepartamento de Desarrollo Académico", textFont);
            signature.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(signature);
            
            document.close();
            return baos.toByteArray();
        }
    }

    @Override
    public byte[] generarConstanciaTutor(Asignacion asignacion, Tutor tutor) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setEncryption(null, null, PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128); // Read-only
            
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            
            Paragraph header = new Paragraph("CONSTANCIA DE PARTICIPACIÓN", titleFont);
            header.setAlignment(Paragraph.ALIGN_CENTER);
            header.setSpacingAfter(40);
            document.add(header);
            
            Paragraph p1 = new Paragraph("A quien corresponda:", textFont);
            p1.setSpacingAfter(20);
            document.add(p1);
            
            Paragraph body = new Paragraph();
            body.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            body.setFont(textFont);
            body.setSpacingAfter(30);
            body.add("El Departamento de Desarrollo Académico otorga la presente constancia a ");
            body.add(new Chunk(tutor.getUsuario().getNombreCompleto(), boldFont));
            body.add(" con número de empleado ");
            body.add(new Chunk(tutor.getUsuario().getNumeroIdentificacion(), boldFont));
            body.add(", por su destacada labor como Tutor del grupo ");
            body.add(new Chunk(asignacion.getGrupo().getNombre(), boldFont));
            body.add(" perteneciente a la carrera de ");
            body.add(new Chunk(asignacion.getGrupo().getCarrera().getNombre(), boldFont));
            body.add(", durante el periodo semestral ");
            body.add(new Chunk(asignacion.getPeriodo().getClave(), boldFont));
            body.add(". Cumpliendo satisfactoriamente con la impartición de las sesiones establecidas en el Programa Institucional de Tutorías.");
            document.add(body);
            
            Paragraph body2 = new Paragraph("Se expide la presente constancia para los fines académicos o administrativos que al interesado(a) convengan, a los " + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'días del mes de' MM 'del año' yyyy")) + ".", textFont);
            body2.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            body2.setSpacingAfter(60);
            document.add(body2);
            
            Paragraph signature = new Paragraph("___________________________________\nJefe(a) del Departamento de Desarrollo Académico", textFont);
            signature.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(signature);
            
            document.close();
            return baos.toByteArray();
        }
    }
}
