package th.ac.bu.science.mit.allappstatscollector.Models;

public class ModelVirusTotalReport {
    String resource;
    int response_code;
    String detection_ratio;
    String detection_percentage;
    String verbose_msg;

    public String Hash() {
        return resource;
    }

    public boolean HasData () {
        if (response_code == 0)
            return false;
        else
            return true;
    }

    @Override
    public String toString() {
        return resource + " : " + response_code + " (" + verbose_msg + ")";
    }
}