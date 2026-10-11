package service;

public record InspectionResult(boolean accepted, String reason) {
    public static InspectionResult accept() {
        return new InspectionResult(true, "Техосмотр пройден");
    }

    public static InspectionResult reject(String reason) {
        return new InspectionResult(false, reason);
    }
}