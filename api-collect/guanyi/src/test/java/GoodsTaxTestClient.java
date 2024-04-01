public class GoodsTaxTestClient {
    public GoodsTaxTestClient() {
    }

    public static void main(String[] args) {
        double consumptionTaxRate = 0.0;
        double valueAddedTaxRate = 0.17;
        double totalAmount = 190.0;
        double postFee = 0.0;
        System.out.println("含税商品金额：" + totalAmount);
        double taxRate = (consumptionTaxRate + valueAddedTaxRate) / (1.0 - consumptionTaxRate) * 0.7;
        System.out.println("综合税率：" + taxRate);
        double amount = totalAmount / (1.0 + taxRate);
        System.out.println("不含税商品金额：" + amount);
        double consumptionDutyAmount = (amount + postFee) / (1.0 - consumptionTaxRate) * consumptionTaxRate;
        double addedValueTaxAmount = (amount + postFee + consumptionDutyAmount) * valueAddedTaxRate;
        System.out.println("应征消费税：" + consumptionDutyAmount * 0.7);
        System.out.println("应征增值税：" + addedValueTaxAmount * 0.7);
    }
}
