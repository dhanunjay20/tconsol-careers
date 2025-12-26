package com.tcon.careers.service;

import com.opencsv.CSVWriter;
import com.tcon.careers.model.JobApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] exportToExcel(List<JobApplication> applications) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Job Applications");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Application ID", "Job Title", "Department", "First Name", "Last Name",
                "Email", "Phone", "Current Location", "Years of Experience", "Current Role",
                "Current Company", "Notice Period", "Expected Salary", "Education",
                "Status", "Application Date", "LinkedIn", "Portfolio", "GitHub"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (JobApplication app : applications) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(app.getApplicationId());
                row.createCell(1).setCellValue(app.getJobTitle());
                row.createCell(2).setCellValue(app.getDepartment());
                row.createCell(3).setCellValue(app.getFirstName());
                row.createCell(4).setCellValue(app.getLastName());
                row.createCell(5).setCellValue(app.getEmail());
                row.createCell(6).setCellValue(app.getPhone());
                row.createCell(7).setCellValue(app.getCurrentLocation());
                row.createCell(8).setCellValue(app.getYearsOfExperience());
                row.createCell(9).setCellValue(app.getCurrentRole());
                row.createCell(10).setCellValue(app.getCurrentCompany() != null ? app.getCurrentCompany() : "");
                row.createCell(11).setCellValue(app.getNoticePeriod());
                row.createCell(12).setCellValue(app.getExpectedSalary() != null ? app.getExpectedSalary() : "");
                row.createCell(13).setCellValue(app.getEducation());
                row.createCell(14).setCellValue(app.getStatus());
                row.createCell(15).setCellValue(app.getApplicationDate().format(DATE_FORMATTER));
                row.createCell(16).setCellValue(app.getLinkedinUrl() != null ? app.getLinkedinUrl() : "");
                row.createCell(17).setCellValue(app.getPortfolioUrl() != null ? app.getPortfolioUrl() : "");
                row.createCell(18).setCellValue(app.getGithubUrl() != null ? app.getGithubUrl() : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            log.info("Exported {} applications to Excel", applications.size());
            return out.toByteArray();
        }
    }

    public String exportToCSV(List<JobApplication> applications) throws IOException {
        StringWriter stringWriter = new StringWriter();

        try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            // Write header
            String[] header = {
                "Application ID", "Job Title", "Department", "First Name", "Last Name",
                "Email", "Phone", "Current Location", "Years of Experience", "Current Role",
                "Current Company", "Notice Period", "Expected Salary", "Education",
                "Status", "Application Date", "LinkedIn", "Portfolio", "GitHub"
            };
            csvWriter.writeNext(header);

            // Write data rows
            for (JobApplication app : applications) {
                String[] data = {
                    app.getApplicationId(),
                    app.getJobTitle(),
                    app.getDepartment(),
                    app.getFirstName(),
                    app.getLastName(),
                    app.getEmail(),
                    app.getPhone(),
                    app.getCurrentLocation(),
                    String.valueOf(app.getYearsOfExperience()),
                    app.getCurrentRole(),
                    app.getCurrentCompany() != null ? app.getCurrentCompany() : "",
                    app.getNoticePeriod(),
                    app.getExpectedSalary() != null ? app.getExpectedSalary() : "",
                    app.getEducation(),
                    app.getStatus(),
                    app.getApplicationDate().format(DATE_FORMATTER),
                    app.getLinkedinUrl() != null ? app.getLinkedinUrl() : "",
                    app.getPortfolioUrl() != null ? app.getPortfolioUrl() : "",
                    app.getGithubUrl() != null ? app.getGithubUrl() : ""
                };
                csvWriter.writeNext(data);
            }

            log.info("Exported {} applications to CSV", applications.size());
            return stringWriter.toString();
        }
    }
}

