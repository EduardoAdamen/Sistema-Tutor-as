package com.itch.tutorias.service.implementjpa;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.model.SesionActividad;
import com.itch.tutorias.model.Tutor;
import com.itch.tutorias.service.IReportePdfService;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportePdfServiceImpl implements IReportePdfService {

    @Override
    public byte[] generarReporteActividadesPat(Tutor tutor, PeriodoSemestral periodo, List<SesionActividad> actividades) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate()); // Landscape for wide tables
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            
            Paragraph title = new Paragraph("Reporte de Actividades PAT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);
            
            String tutorNombre = tutor != null ? tutor.getUsuario().getNombreCompleto() : "Todos los Tutores";
            Paragraph subtitle = new Paragraph("Tutor: " + tutorNombre + "\nPeriodo: " + periodo.getClave(), subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(30);
            document.add(subtitle);
            
            if (actividades == null || actividades.isEmpty()) {
                Paragraph emptyMsg = new Paragraph("No hay actividades registradas en este periodo.", subtitleFont);
                emptyMsg.setAlignment(Element.ALIGN_CENTER);
                document.add(emptyMsg);
            } else {
                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{3f, 4f, 2f, 2f, 2f, 2f});
                
                String[] headers = {"Actividad", "Descripción", "Tipo", "Grupo", "Fecha de Realización", "Estatus"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(new Color(25, 135, 84)); // Success green #198754
                    cell.setPadding(8);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                
                for (SesionActividad act : actividades) {
                    table.addCell(new PdfPCell(new Phrase(act.getActividadCarrera().getActividadGeneral().getTitulo(), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(act.getObservaciones() != null ? act.getObservaciones() : "", cellFont)));
                    table.addCell(new PdfPCell(new Phrase(act.getActividadCarrera().getActividadGeneral().getTipoActividad().name(), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(act.getSesion().getAsignacion().getGrupo().getNombre(), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(act.getSesion().getFecha().format(formatter), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(act.getEvidencia() != null && !act.getEvidencia().isEmpty() ? "Con Evidencia" : "Sin Evidencia", cellFont)));
                }
                
                document.add(table);
            }
            
            document.close();
            return baos.toByteArray();
        }
    }

    @Override
    public byte[] generarReporteActividadesGenerales(List<com.itch.tutorias.model.ActividadPatGeneral> actividades) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            
            Paragraph title = new Paragraph("Catálogo de Actividades PAT Generales", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);
            
            Paragraph subtitle = new Paragraph("Sistema Web de Tutorías", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(30);
            document.add(subtitle);
            
            if (actividades == null || actividades.isEmpty()) {
                Paragraph emptyMsg = new Paragraph("No hay actividades registradas en el catálogo.", subtitleFont);
                emptyMsg.setAlignment(Element.ALIGN_CENTER);
                document.add(emptyMsg);
            } else {
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{3f, 5f, 2f});
                
                String[] headers = {"Título", "Descripción", "Tipo de Actividad"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(new Color(13, 110, 253)); // Primary blue #0d6efd
                    cell.setPadding(8);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                
                for (com.itch.tutorias.model.ActividadPatGeneral act : actividades) {
                    table.addCell(new PdfPCell(new Phrase(act.getTitulo(), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(act.getDescripcion() != null ? act.getDescripcion() : "", cellFont)));
                    table.addCell(new PdfPCell(new Phrase(act.getTipoActividad().name(), cellFont)));
                }
                
                document.add(table);
            }
            
            document.close();
            return baos.toByteArray();
        }
    }
}
