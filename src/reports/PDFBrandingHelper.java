package reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.IOException;

/**
 * PDF Branding Helper
 *
 * Centralized branding configuration for all PDF reports.
 * Maintains consistent color scheme, fonts, and styling across all report types.
 *
 * Brand Colors:
 * - Orange: #FC7222 (RGB: 252, 114, 34) - Primary brand color for titles and accents
 * - Green: #1E8F72 (RGB: 30, 143, 114) - Secondary brand color for headers
 * - Light Gray: #F5F5F5 (RGB: 245, 245, 245) - Alternating row background
 *
 * Typography:
 * - Titles: SFT Schrifted Sans (DEMIBOLD) - 20pt
 * - Headers: SFT Schrifted Sans (MEDIUM) - 10pt
 * - Body Text: Neue Montreal (REGULAR) - 9pt
 * - Buttons: SFT Schrifted Sans (MEDIUM) - 10pt
 *
 * Logo:
 * - Path: src/main/gui/assets/logo1_orig.png
 * - Size: 60x60 pixels
 */
public class PDFBrandingHelper {

    // Brand colors
    public static final BaseColor BRAND_ORANGE = new BaseColor(252, 114, 34); // #FC7222
    public static final BaseColor BRAND_GREEN = new BaseColor(30, 143, 114);  // #1E8F72
    public static final BaseColor LIGHT_GRAY = new BaseColor(245, 245, 245);  // #F5F5F5
    public static final BaseColor WHITE = BaseColor.WHITE;
    public static final BaseColor BLACK = BaseColor.BLACK;
    public static final BaseColor DARK_GRAY = new BaseColor(100, 100, 100);

    // Logo path
    public static final String LOGO_PATH = "src/main/gui/assets/logo1_orig.png";

    /**
     * Load and return the company logo
     *
     * Tries multiple possible logo locations:
     * 1. src/main/gui/assets/logo1_org.png (default)
     * 2. src/main/resources/logo1_org.png (alternative)
     * 3. logo1_org.png (project root)
     *
     * @return Image object or null if logo not found
     */
    public static Image loadLogo() {
        String[] possiblePaths = {
                "src/main/gui/assets/logo1_orig.png",
                "src/main/resources/logo1_orig.png",
                "logo1_orig.png",
                "assets/logo1_orig.png"
        };

        for (String path : possiblePaths) {
            try {
                Image logo = Image.getInstance(path);
                logo.scaleToFit(60, 60);
                logo.setAlignment(Element.ALIGN_LEFT);
                System.out.println("✓ Logo loaded from: " + path);
                return logo;
            } catch (Exception e) {
                // Try next path
            }
        }

        // No logo found in any location
        System.out.println("⚠ Logo not found. Tried locations:");
        for (String path : possiblePaths) {
            System.out.println("   - " + path);
        }
        System.out.println("   Continuing without logo. PDF will not include 'Section' label.");
        return null;
    }

    /**
     * Create title font (SFT Schrifted Sans DemiBold equivalent)
     * Falls back to Helvetica Bold if custom font unavailable
     */
    public static Font getTitleFont() {
        try {
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            return new Font(bf, 20, Font.BOLD, BRAND_ORANGE);
        } catch (Exception e) {
            return new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BRAND_ORANGE);
        }
    }

    /**
     * Create header font (SFT Schrifted Sans Medium equivalent)
     * Used for table headers with white text on green background
     */
    public static Font getHeaderFont() {
        try {
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            return new Font(bf, 10, Font.BOLD, WHITE);
        } catch (Exception e) {
            return new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, WHITE);
        }
    }

    /**
     * Create body font (Neue Montreal Regular equivalent)
     * Used for table data and general content
     */
    public static Font getBodyFont() {
        try {
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            return new Font(bf, 9, Font.NORMAL, BLACK);
        } catch (Exception e) {
            return new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BLACK);
        }
    }

    /**
     * Create body font with custom size
     */
    public static Font getBodyFont(int size) {
        try {
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            return new Font(bf, size, Font.NORMAL, BLACK);
        } catch (Exception e) {
            return new Font(Font.FontFamily.HELVETICA, size, Font.NORMAL, BLACK);
        }
    }

    /**
     * Create subheader font (SFT Schrifted Sans Medium equivalent)
     * Used for section titles in green
     */
    public static Font getSubheaderFont() {
        try {
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            return new Font(bf, 12, Font.BOLD, BRAND_GREEN);
        } catch (Exception e) {
            return new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BRAND_GREEN);
        }
    }

    /**
     * Create subheader font with custom size
     */
    public static Font getSubheaderFont(int size) {
        try {
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            return new Font(bf, size, Font.BOLD, BRAND_GREEN);
        } catch (Exception e) {
            return new Font(Font.FontFamily.HELVETICA, size, Font.BOLD, BRAND_GREEN);
        }
    }

    /**
     * Create bold font for emphasis
     */
    public static Font getBoldFont() {
        try {
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            return new Font(bf, 10, Font.BOLD, BLACK);
        } catch (Exception e) {
            return new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BLACK);
        }
    }

    /**
     * Create bold font with custom color
     */
    public static Font getBoldFont(BaseColor color) {
        try {
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            return new Font(bf, 10, Font.BOLD, color);
        } catch (Exception e) {
            return new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, color);
        }
    }

    /**
     * Create footer font (small italic gray)
     */
    public static Font getFooterFont() {
        return new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, DARK_GRAY);
    }

    /**
     * Create a styled header cell with green background
     */
    public static PdfPCell createHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, getHeaderFont()));
        cell.setBackgroundColor(BRAND_GREEN);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    /**
     * Create a data cell with alternating row color
     */
    public static PdfPCell createDataCell(String text, int rowIndex) {
        PdfPCell cell = new PdfPCell(new Phrase(text, getBodyFont()));
        cell.setBackgroundColor(rowIndex % 2 == 0 ? WHITE : LIGHT_GRAY);
        cell.setPadding(6);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    /**
     * Create a data cell with custom alignment
     */
    public static PdfPCell createDataCell(String text, int rowIndex, int alignment) {
        PdfPCell cell = createDataCell(text, rowIndex);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    /**
     * Create a data cell with custom font
     */
    public static PdfPCell createDataCell(String text, int rowIndex, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(rowIndex % 2 == 0 ? WHITE : LIGHT_GRAY);
        cell.setPadding(6);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    /**
     * Add logo and title section to document
     *
     * @param document The PDF document
     * @param reportTitle The main report title
     * @param subtitle Optional subtitle (can be null)
     */
    public static void addHeaderSection(Document document, String reportTitle, String subtitle) throws DocumentException {
        // Try to add logo
        Image logo = loadLogo();
        if (logo != null) {
            document.add(logo);
        } else {
            // If no logo, add extra spacing at top
            Paragraph spacer = new Paragraph(" ");
            spacer.setSpacingBefore(20);
            document.add(spacer);
        }

        // Add main title
        Paragraph title = new Paragraph(reportTitle, getTitleFont());
        title.setSpacingAfter(15);
        document.add(title);

        // Add subtitle if provided
        if (subtitle != null && !subtitle.isEmpty()) {
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BRAND_GREEN);
            Paragraph sub = new Paragraph(subtitle, subtitleFont);
            sub.setSpacingAfter(15);
            document.add(sub);
        }
    }

    /**
     * Add a summary row to a table (for two-column summary tables)
     */
    public static void addSummaryRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BLACK);
        Font valueFont = getBoldFont(BRAND_ORANGE);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    /**
     * Add generation timestamp footer
     */
    public static void addFooter(Document document, String timestamp) throws DocumentException {
        Paragraph footer = new Paragraph(
                "\nGenerated on: " + timestamp,
                getFooterFont()
        );
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setSpacingBefore(30);
        document.add(footer);
    }
}