package common;

public enum ExchangeType {

    USD("달러", 1303.00),
    JPY_100("엔", 910.97),
    EUR("유로", 1423.92),
    CNY("위안", 182.49),
    GBP("파운드", 1650.18);


    private String name;
    private double rate;
    ExchangeType(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
