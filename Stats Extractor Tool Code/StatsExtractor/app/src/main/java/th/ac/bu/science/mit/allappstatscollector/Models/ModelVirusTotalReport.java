package th.ac.bu.science.mit.allappstatscollector.Models;

public class ModelVirusTotalReport {
    String resource;
    int response_code;
    String detection_ratio;
    String verbose_message;

    @Override
    public String toString() {
        return resource + " : " + response_code + " (" + verbose_message + ")";
    }
}