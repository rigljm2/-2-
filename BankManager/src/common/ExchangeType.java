package common;

public enum ExchangeType {

    USD("�޷�", 1303.00),
    JPY_100("��", 910.97),
    EUR("����", 1423.92),
    CNY("����", 182.49),
    GBP("�Ŀ��", 1650.18);


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
