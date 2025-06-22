package service;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import Utils.JDBCUtil;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ExportToPDF {
    public static void prescriptionToPDF(String idPres) {
        String query="SELECT \r\n"
        		+ "    p.id AS prescription_id,\r\n"
        		+ "    pat.name AS 'Tên bệnh nhân',\r\n"
        		+ "    doc_emp.name AS 'Tên bác sĩ khám',\r\n"
        		+ "    d.name AS 'Tên thuốc',\r\n"
        		+ "    pd.quantity as 'Số lượng',\r\n"
        		+ "    pd.morningDose as 'Liều sáng',\r\n"
        		+ "    pd.noonDose as 'Liều chiều',\r\n"
        		+ "    pd.eveningDose as 'Liều tối',\r\n"
        		+ "    pat.address as 'Địa chỉ bệnh nhân',\r\n"
        		+ "    p.diagnosis as 'Diagnosis',\r\n"
        		+ "    pat.birthDate as 'Ngày sinh',\r\n"
        		+ "    pat.gender as 'Gender',\r\n"
        		+ "    p.advice as 'Lời khuyên',\r\n"
        		+ "    p.symptom as 'Triệu chứng',\r\n"
        		+ "    p.preDate as 'Ngày tạo đơn thuốc'\r\n"
        		+ "FROM Prescription p\r\n"
        		+ "JOIN Patient pat ON p.patient_id = pat.id\r\n"
        		+ "JOIN Doctor doc ON p.doctor_id = doc.id\r\n"
        		+ "JOIN Employee doc_emp ON doc.id = doc_emp.id\r\n"
        		+ "JOIN PrescriptionDrugDetail pd ON p.id = pd.prescription_id\r\n"
        		+ "JOIN Drug d ON pd.drug_id = d.id\r\n"
        		+ "WHERE p.id = "+idPres;

        try {
        	String baseFont="C:/Windows/Fonts/times.ttf";
        	
            Connection conn = JDBCUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            //lấy tên,tuổi,địa chỉ,giới tính,cccd
            //triệu chứng chuẩn đoán
            //lời dặn, hẹn tái khám
            //tên nhân viên/bs

            String name = "", address = "";
            int age;
            String sex;
            String symptom = "", diagnosis = "";
            String advice = "", appoiment = "";
            String nameDentist = "";
            Date dateBirth = null;

            rs.next();
            int id = rs.getInt("prescription_id");
            name = rs.getString("Tên bệnh nhân");
            address = rs.getString("Địa chỉ bệnh nhân");
            diagnosis = rs.getString("Diagnosis");
            symptom=rs.getString("Triệu chứng");
            advice = rs.getString("Lời khuyên");
            Timestamp timestap=rs.getTimestamp("Ngày tạo đơn thuốc");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            appoiment = formatter.format(timestap.toLocalDateTime().toLocalDate().plusDays(7));
            nameDentist = rs.getString("Tên bác sĩ khám");
            dateBirth = rs.getDate("Ngày sinh");
            sex = rs.getBoolean("Gender") ? "Nam" : "Nữ";
            age = LocalDate.now().getYear() - dateBirth.toLocalDate().getYear();
            rs.close();

            rs = stmt.executeQuery(query);

            //tạo tên file theo định dạng DonThoc + cccd+ngay thang nam
            String filePath = "Medicine\\"+"DonThoc_"+id+"-"+timestap.toLocalDateTime().toLocalDate().toString()+".pdf";
            //
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            //Các font sẽ sử dụng
            BaseFont bsT = BaseFont.createFont(baseFont, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font fontHeader = new Font(bsT, 12);
            Font fontBoldPrescription = new Font(bsT, 14, Font.BOLD);
            Font fontT12Bold = new Font(bsT, 12, Font.BOLD);
            Font fontT12I = new Font(bsT, 12, Font.ITALIC);

            // Tạo bảng
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.setSpacingAfter(10f); // Thêm khoảng cách sau bảng

            // Ô 1
            Phrase phrase1 = new Phrase("Sở Y Tế TP.HCM\nPhòng Khám Nha Khoa LMK", fontHeader);
            PdfPCell cell1 = new PdfPCell(phrase1);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell1);

            // Ô 2
            Phrase phrase2 = new Phrase("Số 450, Lê Văn Việt, Quận 9\nHotline: 090012109", fontHeader);
            PdfPCell cell2 = new PdfPCell(phrase2);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell2);

            document.add(table);

            // Tiêu đề "ĐƠN THUỐC"
            Paragraph paragraph = new Paragraph("ĐƠN THUỐC", fontBoldPrescription);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingBefore(10f); // Thêm khoảng cách trước đoạn văn nhưng k có tác dụng:)
            document.add(paragraph);

            //tạo bảng thông tin người nhận

            PdfPTable tableInfo = new PdfPTable(3);
            tableInfo.setWidthPercentage(80);
            tableInfo.setWidths(new float[]{4, 2, 2});
            tableInfo.setHorizontalAlignment(Element.ALIGN_CENTER);
            //ô1
            PdfPCell cellTableIf1 = new PdfPCell(new Phrase("\nHọ tên: " + name + "\nĐịa chỉ: " + address , fontHeader));
            cellTableIf1.setBorder(Rectangle.NO_BORDER);
            cellTableIf1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellTableIf1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tableInfo.addCell(cellTableIf1);
            //ô2
            PdfPCell cellTableIf2 = new PdfPCell(new Phrase("Tuổi: " + age, fontHeader));
            cellTableIf2.setBorder(Rectangle.NO_BORDER);
            cellTableIf2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTableIf2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tableInfo.addCell(cellTableIf2);
            //ô3
            PdfPCell cellTableIf3 = new PdfPCell(new Phrase("Giới tính: " + sex, fontHeader));
            cellTableIf3.setBorder(Rectangle.NO_BORDER);
            cellTableIf3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTableIf3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tableInfo.addCell(cellTableIf3);

            document.add(tableInfo);

            //tạo bảng triệu chứng và chẩn đoán
            PdfPTable tableDiagnose = new PdfPTable(1);
            tableDiagnose.setWidthPercentage(80);
            tableDiagnose.setHorizontalAlignment(Element.ALIGN_CENTER);

            //tạo dòng chứa triệu chứng và chẩn đoán
            Paragraph p = new Paragraph();
            p.add(new Chunk("Triệu chứng: ", fontT12Bold));
            p.add(new Chunk(symptom, fontHeader));
            PdfPCell cellPDFP1 = new PdfPCell(p);
            cellPDFP1.setBorder(Rectangle.NO_BORDER);
            tableDiagnose.addCell(cellPDFP1);

            Paragraph p1 = new Paragraph();
            p1.add(new Chunk("Chẩn đoán: ", fontT12Bold));
            p1.add(new Chunk(diagnosis, fontHeader));
            PdfPCell cellPDFP2 = new PdfPCell(p1);
            cellPDFP2.setBorder(Rectangle.NO_BORDER);
            tableDiagnose.addCell(cellPDFP2);

            document.add(tableDiagnose);

            //Tạo bảng đơn thuốc
            PdfPTable tableMeDicine = new PdfPTable(2);
            tableMeDicine.setWidthPercentage(80);
            tableMeDicine.setWidths(new float[]{7, 3});
            tableMeDicine.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph p2 = new Paragraph();
            p2.add(Chunk.NEWLINE);
            p2.add(new Chunk("Tên thuốc - hoạt chất", fontT12Bold));
            PdfPCell cellPDFP3 = new PdfPCell(p2);
            cellPDFP3.setBorder(Rectangle.NO_BORDER);
            tableMeDicine.addCell(cellPDFP3);

            Paragraph p3 = new Paragraph();
            p3.add(Chunk.NEWLINE);
            p3.add(new Chunk("Số lượng", fontT12Bold));
            PdfPCell cellPDFP4 = new PdfPCell(p3);
            cellPDFP4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellPDFP4.setBorder(Rectangle.NO_BORDER);
            tableMeDicine.addCell(cellPDFP4);

            int i = 1;
            while (rs.next()) {
                String dosageS = rs.getString("Liều sáng");
                String dosageC = rs.getString("Liều chiều");
                String dosageT = rs.getString("Liều tối");
                String nameMedicine = rs.getString("Tên thuốc");
                String quantity = rs.getString("Số lượng");
                Paragraph p4 = new Paragraph();
                p4.add(new Chunk(i + "/ " + nameMedicine, fontT12Bold));
                p4.add(Chunk.NEWLINE);
                p4.add(new Chunk("Sáng: "+dosageS+" viên, Chiều: "+dosageC+" viên, Tối: "+dosageT+" viên", fontT12I));
                PdfPCell cellPDFP5 = new PdfPCell(p4);
                cellPDFP5.setBorder(Rectangle.NO_BORDER);
                tableMeDicine.addCell(cellPDFP5);

                Paragraph p5 = new Paragraph();
                p5.add(new Chunk("x" + quantity, fontHeader));
                PdfPCell cellPDFP6 = new PdfPCell(p5);
                cellPDFP6.setBorder(Rectangle.NO_BORDER);
                cellPDFP6.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableMeDicine.addCell(cellPDFP6);
                ++i;
            }
            document.add(tableMeDicine);

            //tạo bảng lời dặn và hẹn khám
            PdfPTable tableAdvice = new PdfPTable(1);
            tableInfo.setWidthPercentage(80);
            tableInfo.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph p6 = new Paragraph();
            p6.add(Chunk.NEWLINE);
            p6.add(new Chunk("Lời dặn: " + advice, fontHeader));
            PdfPCell cellPDFP7 = new PdfPCell(p6);
            cellPDFP7.setBorder(Rectangle.NO_BORDER);
            cellPDFP7.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableAdvice.addCell(cellPDFP7);

            Paragraph p7 = new Paragraph();
            p7.add(new Chunk("Hẹn tái khám: " + appoiment, fontHeader));
            PdfPCell cellPDFP8 = new PdfPCell(p7);
            cellPDFP8.setBorder(Rectangle.NO_BORDER);
            cellPDFP8.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableAdvice.addCell(cellPDFP8);

            document.add(tableAdvice);

            PdfPTable tableSign = new PdfPTable(2);
            tableSign.setWidthPercentage(80);
            tableSign.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell cellTmp = new PdfPCell();
            cellTmp.setBorder(Rectangle.NO_BORDER);
            tableSign.addCell(cellTmp);
            
            LocalDate datePre=timestap.toLocalDateTime().toLocalDate();

            Paragraph p8 = new Paragraph();
            p8.add(new Chunk("Ngày " + datePre.getDayOfMonth() + " tháng " + datePre.getMonthValue() + " năm " + datePre.getYear(), fontHeader));
            p8.add(Chunk.NEWLINE);
            p8.add(new Chunk("Bác sĩ khám bệnh", fontT12Bold));
            p8.add(Chunk.NEWLINE);
            p8.add(new Chunk("(Ký, ghi rõ họ tên)", fontT12I));
            p8.add(Chunk.NEWLINE);
            p8.add(Chunk.NEWLINE);
            p8.add(new Chunk("[Đã ký]", fontHeader));
            p8.add(Chunk.NEWLINE);
            p8.add(Chunk.NEWLINE);
            p8.add(new Chunk("BS. "+nameDentist, fontT12Bold));
            PdfPCell cellPDFP9 = new PdfPCell(p8);
            cellPDFP9.setBorder(Rectangle.NO_BORDER);
            cellPDFP9.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableSign.addCell(cellPDFP9);

            document.add(tableSign);

            document.close();
            System.out.println("Đã tạo file PDF: " + filePath);
            //tự mở file
            File pdfFile = new File(filePath);
            
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(pdfFile);
            } else {
                System.out.println("Hệ thống không hỗ trợ mở file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
	}
    public static void billToPDF(String idBill) {
        String query="SELECT \n" +
                "    p.id AS 'Prescription ID',\n" +
                "    pa.name AS 'Tên bệnh nhân',\n" +
                "    pa.address AS 'Địa chỉ bệnh nhân',\n" +
                "    s.name AS 'Tên dịch vụ',\n" +
                "    s.price AS 'Giá dịch vụ',\n" +
                "    psd.quantity AS 'Số lượng dịch vụ',\n" +
                "    d.name AS 'Tên thuốc',\n" +
                "    pdd.quantity AS 'Số lượng',\n" +
                "    p.preDate AS 'Ngày tạo bill',\n" +
                "    d.price AS 'Giá tiền thuốc'\n" +
                "FROM Prescription p\n" +
                "JOIN Patient pa ON p.patient_id = pa.id\n" +
                "LEFT JOIN PrescriptionServiceDetail psd ON p.id = psd.prescription_id\n" +
                "LEFT JOIN Service s ON psd.service_id = s.id\n" +
                "LEFT JOIN PrescriptionDrugDetail pdd ON p.id = pdd.prescription_id\n" +
                "LEFT JOIN Drug d ON pdd.drug_id = d.id\n" +
                "WHERE p.id ="+idBill;
        try {
            String baseFont="C:/Windows/Fonts/times.ttf";

            Connection conn = JDBCUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            //lưu thông tin của người bệnh
            String name="";
            String address="";
            String billID="";
            LocalDate billDate=null;

            //duyệt 1 lần để lấy tên của người bênh
            rs.next();
            int id=rs.getInt("Prescription ID");
            name=rs.getString("Tên bệnh nhân");
            address=rs.getString("Địa chỉ bệnh nhân");
            billID=rs.getString("Prescription ID");
            Timestamp billDateT=rs.getTimestamp("Ngày tạo bill");
            billDate=billDateT.toLocalDateTime().toLocalDate();
            rs.close();


            String filePath = "Bill\\"+"Bill_"+id+"-"+billDate.toString()+".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            //font sử dụng
            BaseFont bsT = BaseFont.createFont(baseFont, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font fontHeader = new Font(bsT, 12);
            Font fontBoldHeader = new Font(bsT, 14, Font.BOLD);
            Font fontT12Bold = new Font(bsT, 12, Font.BOLD);
            Font fontT16Bold = new Font(bsT, 16, Font.BOLD);
            Font fontT12I = new Font(bsT, 12, Font.ITALIC);
            Paragraph pEnter = new Paragraph(" ");
            DecimalFormat formatter = new DecimalFormat("#,###");

            //tạo bảng header
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph pH = new Paragraph();
            pH.add(new Chunk("\nPHÒNG KHÁM NHA KHOA LMK", fontBoldHeader));
            PdfPCell cellH = new PdfPCell(pH);
            cellH.setBorder(Rectangle.NO_BORDER);
            cellH.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellH);

            Paragraph pI = new Paragraph();
            pI.add(new Chunk("Địa chỉ: 450 Lê Văn Việt, Tăng Nhơn Phú A, TP. Thủ Đức", fontHeader));
            pI.add(Chunk.NEWLINE);
            pI.add(new Chunk("Điện thoại: 090012109", fontHeader));
            PdfPCell cellI = new PdfPCell(pI);
            cellI.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellI.setBorder(Rectangle.NO_BORDER);
            table.addCell(cellI);
            document.add(table);

            document.add(pEnter);
            Paragraph pTitle = new Paragraph();
            pTitle.add(new Chunk("HOÁ ĐƠN KHÁM BỆNH", fontT16Bold));
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);

            //tạo bảng lưu thong tin
            PdfPTable tableInfo = new PdfPTable(2);
            tableInfo.setWidthPercentage(80);
            tableInfo.setWidths(new float[]{6, 3});
            tableInfo.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell cellInfo = new PdfPCell(new Paragraph());
            cellInfo.setBorder(Rectangle.NO_BORDER);
            tableInfo.addCell(cellInfo);
            Paragraph pCode = new Paragraph();
            pCode.add(new Chunk("Số: B"+String.format("%03d", Integer.parseInt(billID)), fontHeader));
            PdfPCell cellCode = new PdfPCell(pCode);
            cellCode.setBorder(Rectangle.NO_BORDER);
            cellCode.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableInfo.addCell(cellCode);

            Paragraph pName = new Paragraph();
            pName.add(new Chunk("Tên khách hàng: "+name, fontHeader));
            pName.setAlignment(Element.ALIGN_LEFT);
            PdfPCell cellName = new PdfPCell(pName);
            cellName.setBorder(Rectangle.NO_BORDER);
            tableInfo.addCell(cellName);
            tableInfo.addCell(cellInfo);
            Paragraph pAddress = new Paragraph();
            pAddress.add(new Chunk("Địa chỉ: "+address, fontHeader));
            pAddress.setAlignment(Element.ALIGN_LEFT);
            PdfPCell cellAddress = new PdfPCell(pAddress);
            cellAddress.setBorder(Rectangle.NO_BORDER);
            tableInfo.addCell(cellAddress);
            tableInfo.addCell(cellInfo);

            document.add(tableInfo);

            //tạo bảng tính tiền
            document.add(pEnter);
            String[] nameCollums = {"TT", "NỘI DUNG DỊCH VỤ", "SL", "ĐƠN GIÁ", "THÀNH TIỀN"};
            PdfPTable tableBill = new PdfPTable(5);
            tableBill.setWidthPercentage(80);
            tableBill.setWidths(new float[]{1, 5, 1, 2, 3});
            tableBill.setHorizontalAlignment(Element.ALIGN_CENTER);

            for (int i = 0; i < nameCollums.length; i++) {
                Paragraph pTT = new Paragraph();
                pTT.add(new Chunk(nameCollums[i], fontT12Bold));
                pTT.setAlignment(Element.ALIGN_CENTER);
                PdfPCell cellTT = new PdfPCell(pTT);
                cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableBill.addCell(cellTT);
            }
            rs=stmt.executeQuery(query);
            // Lấy tên, số lượng và giá dịch vụ
            Map<String, BigDecimal> listService = new LinkedHashMap<>();
            List<Integer> listQuantityService = new ArrayList<>();

            rs = stmt.executeQuery(query);
            while (rs.next()) {
                String nameService = rs.getString("Tên dịch vụ");
                if (nameService != null) {
                    BigDecimal priceService = rs.getBigDecimal("Giá dịch vụ");
                    int quantityService = rs.getInt("Số lượng dịch vụ");

                    listService.put(nameService, priceService);
                    listQuantityService.add(quantityService);
                }
            }

            BigDecimal sumTotal = BigDecimal.ZERO;
            int j = 0;
            for (Map.Entry<String, BigDecimal> entry : listService.entrySet()) {
                String nameService = entry.getKey();
                BigDecimal priceService = entry.getValue();
                int quantity = listQuantityService.get(j);
                BigDecimal totalService = priceService.multiply(BigDecimal.valueOf(quantity));

                Paragraph pTT = new Paragraph(new Chunk((j + 1) + "", fontHeader));
                PdfPCell cellTT = new PdfPCell(pTT);
                cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableBill.addCell(cellTT);

                Paragraph pContent = new Paragraph(new Chunk(nameService, fontHeader));
                PdfPCell cellContent = new PdfPCell(pContent);
                cellContent.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableBill.addCell(cellContent);

                Paragraph pNum = new Paragraph(new Chunk(quantity + "", fontHeader));
                PdfPCell cellNum = new PdfPCell(pNum);
                cellNum.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableBill.addCell(cellNum);

                Paragraph pPrice = new Paragraph(new Chunk(formatter.format(priceService), fontHeader));
                PdfPCell cellPrice = new PdfPCell(pPrice);
                cellPrice.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableBill.addCell(cellPrice);

                Paragraph pTotal = new Paragraph(new Chunk(formatter.format(totalService), fontHeader));
                PdfPCell cellTotal = new PdfPCell(pTotal);
                cellTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableBill.addCell(cellTotal);

                sumTotal = sumTotal.add(totalService);
                j++;
            }


            for (int i = 0; i < nameCollums.length+1; i++) {
                Paragraph pTT = new Paragraph();
                pTT.add(new Chunk(" ", fontHeader));
                pTT.setAlignment(Element.ALIGN_CENTER);
                PdfPCell cellTT = new PdfPCell(pTT);
                cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableBill.addCell(cellTT);
            }
            Paragraph pTT = new Paragraph();
            pTT.add(new Chunk("TỔNG CỘNG", fontT12Bold));
            pTT.setAlignment(Element.ALIGN_CENTER);
            PdfPCell cellTT = new PdfPCell(pTT);
            cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableBill.addCell(cellTT);

            for (int i = 0; i < 2; i++) {
                pTT = new Paragraph();
                pTT.add(new Chunk(" ", fontHeader));
                pTT.setAlignment(Element.ALIGN_CENTER);
                cellTT = new PdfPCell(pTT);
                cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableBill.addCell(cellTT);
            }

            pTT = new Paragraph();
            pTT.add(new Chunk(" "+formatter.format(sumTotal), fontHeader));
            pTT.setAlignment(Element.ALIGN_CENTER);
            cellTT = new PdfPCell(pTT);
            cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableBill.addCell(cellTT);

            document.add(tableBill);

            //tạo bảng chứa bill thuốc
            document.add(pEnter);

            String[] nameCollumsMedicine = {"TT", "TÊN THUỐC", "SL", "ĐƠN GIÁ", "THÀNH TIỀN"};
            PdfPTable tableBillMedicine = new PdfPTable(5);
            tableBillMedicine.setWidthPercentage(80);
            tableBillMedicine.setWidths(new float[]{1, 5, 1, 2, 3});
            tableBillMedicine.setHorizontalAlignment(Element.ALIGN_CENTER);

            for (int i = 0; i < nameCollumsMedicine.length; i++) {
                pTT = new Paragraph();
                pTT.add(new Chunk(nameCollumsMedicine[i], fontT12Bold));
                pTT.setAlignment(Element.ALIGN_CENTER);
                cellTT = new PdfPCell(pTT);
                cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableBillMedicine.addCell(cellTT);
            }
            int k=0;
            BigDecimal sumMedicine=BigDecimal.ZERO;
            rs=stmt.executeQuery(query);
            Map<String,BigDecimal> listMedicine=new HashMap<String, BigDecimal>();
            ArrayList<Integer> listQuantityMedicine=new ArrayList<Integer>();
            while (rs.next()){
                String nameMedicine = rs.getString("Tên thuốc");
                BigDecimal priceMedicine = rs.getBigDecimal("Giá tiền thuốc");
                listMedicine.put(nameMedicine, priceMedicine);
                
                int quantityMedicine = rs.getInt("Số lượng");
                listQuantityMedicine.add(quantityMedicine);
            }
            for (Map.Entry<String, BigDecimal> entry : listMedicine.entrySet()) {
                String nameMedicine = entry.getKey();
                BigDecimal priceMedicine = entry.getValue();

              BigDecimal totalMedicine=priceMedicine.multiply(new BigDecimal(listQuantityMedicine.get(k)));
                
                
                
              pTT = new Paragraph();
              pTT.add(new Chunk((k+1)+"", fontHeader));
              pTT.setAlignment(Element.ALIGN_CENTER);
              cellTT = new PdfPCell(pTT);
              cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
              tableBillMedicine.addCell(cellTT);

              Paragraph pContent = new Paragraph();
              pContent.add(new Chunk(nameMedicine, fontHeader));
              pContent.setAlignment(Element.ALIGN_CENTER);
              PdfPCell cellContent = new PdfPCell(pContent);
              cellContent.setHorizontalAlignment(Element.ALIGN_CENTER);
              tableBillMedicine.addCell(cellContent);

              Paragraph pNum = new Paragraph();
              pNum.add(new Chunk(listQuantityMedicine.get(k)+"", fontHeader));
              pNum.setAlignment(Element.ALIGN_CENTER);
              PdfPCell cellNum = new PdfPCell(pNum);
              cellNum.setHorizontalAlignment(Element.ALIGN_CENTER);
              tableBillMedicine.addCell(cellNum);

              Paragraph pPrice = new Paragraph();
              pPrice.add(new Chunk(""+formatter.format(priceMedicine), fontHeader));
              pPrice.setAlignment(Element.ALIGN_CENTER);
              PdfPCell cellprice = new PdfPCell(pPrice);
              cellprice.setHorizontalAlignment(Element.ALIGN_CENTER);
              tableBillMedicine.addCell(cellprice);

              Paragraph pTotal = new Paragraph();
              pTotal.add(new Chunk(formatter.format(totalMedicine)+"", fontHeader));
              sumMedicine=sumMedicine.add(totalMedicine);
              pTotal.setAlignment(Element.ALIGN_CENTER);
              PdfPCell cellTotal = new PdfPCell(pTotal);
              cellTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
              tableBillMedicine.addCell(cellTotal);
              k++;
            }

            
            for (int i = 0; i < nameCollumsMedicine.length+1; i++) {
                pTT = new Paragraph();
                pTT.add(new Chunk(" ", fontHeader));
                pTT.setAlignment(Element.ALIGN_CENTER);
                cellTT = new PdfPCell(pTT);
                cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableBillMedicine.addCell(cellTT);
            }
            pTT = new Paragraph();
            pTT.add(new Chunk("TỔNG CỘNG", fontT12Bold));
            pTT.setAlignment(Element.ALIGN_CENTER);
            cellTT = new PdfPCell(pTT);
            cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableBillMedicine.addCell(cellTT);

            for (int i = 0; i < 2; i++) {
                pTT = new Paragraph();
                pTT.add(new Chunk(" ", fontHeader));
                pTT.setAlignment(Element.ALIGN_CENTER);
                cellTT = new PdfPCell(pTT);
                cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableBillMedicine.addCell(cellTT);
            }

            pTT = new Paragraph();
            pTT.add(new Chunk(" "+formatter.format(sumMedicine), fontHeader));
            pTT.setAlignment(Element.ALIGN_CENTER);
            cellTT = new PdfPCell(pTT);
            cellTT.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableBillMedicine.addCell(cellTT);

            document.add(tableBillMedicine);

            PdfPTable tableTotle=new PdfPTable(1);
            tableTotle.setWidthPercentage(80);
            tableTotle.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph pTotal = new Paragraph();
            document.add(pEnter);
            pTotal.add(new Chunk("TỔNG SỐ TIỀN PHẢI THANH TOÁN: ", fontT12Bold));
            pTotal.add(new Chunk((formatter.format(sumMedicine.add(sumTotal)))+" VND", fontHeader));
            PdfPCell cellTotal = new PdfPCell(pTotal);
            cellTotal.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellTotal.setBorder(Rectangle.NO_BORDER);
            tableTotle.addCell(cellTotal);
            document.add(tableTotle);

            document.add(pEnter);
            PdfPTable tableEnd=new PdfPTable(2);
            tableEnd.setWidthPercentage(80);
            tableEnd.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph pEnd1 = new Paragraph();
            pEnd1.add(new Chunk("Bệnh nhân", fontHeader));
            pEnd1.add(Chunk.NEWLINE);
            pEnd1.add(new Chunk("(Ký, ghi rõ họ tên)",fontHeader));
            PdfPCell cellEnd1 = new PdfPCell(pEnd1);
            cellEnd1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellEnd1.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cellEnd1.setBorder(Rectangle.NO_BORDER);
            tableEnd.addCell(cellEnd1);

            Paragraph pEnd2 = new Paragraph();
            pEnd2.add(new Chunk("Ngày "+billDate.getDayOfMonth()+" tháng "+billDate.getMonthValue()+" năm "+billDate.getYear(), fontHeader));
            pEnd2.add(Chunk.NEWLINE);
            pEnd2.add(Chunk.NEWLINE);
            pEnd2.add(new Chunk("Nhân viên thu ngân", fontHeader));
            pEnd2.add(Chunk.NEWLINE);
            pEnd2.add(new Chunk("(Ký, ghi rõ họ tên)",fontHeader));
            PdfPCell cellEnd2 = new PdfPCell(pEnd2);
            cellEnd2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellEnd2.setBorder(Rectangle.NO_BORDER);
            tableEnd.addCell(cellEnd2);

            document.add(tableEnd);

            document.close();
            System.out.println("Đã tạo file "+filePath);
            
            File pdfFile = new File(filePath);
            
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(pdfFile);
            } else {
                System.out.println("Hệ thống không hỗ trợ mở file.");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void main(String[] args) {
    	//đây là test
    	
        //strIDPatient sẽ là nơi truyền vào id của người bệnh
    	//lí do chọn String là để tránh trường hợp id quá dài
        String strIDPres=1+"";

        ExportToPDF.billToPDF(strIDPres);
        ExportToPDF.prescriptionToPDF(strIDPres);
    }
}