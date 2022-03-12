import com.dntech.demo.dto.CancelOrder;
import com.dntech.demo.dto.ModifyOrder;
import com.dntech.demo.dto.NewOrder;

import javax.lang.model.element.Modifier;

public class OrderManagementApplication {
    public static void main(String[] args) {
        NewOrder newOrder = new NewOrder("myNewOrderId");
        CancelOrder cancelOrder = new CancelOrder("myCancelOrderId");
        ModifyOrder modifyOrder = new ModifyOrder("myModifyOrderId");

        System.out.println(newOrder.getOrderId());
        System.out.println(newOrder.getOrderState());

        System.out.println(cancelOrder.getOrderId());
        System.out.println(cancelOrder.getOrderState());

        System.out.println(modifyOrder.getOrderId());
        System.out.println(modifyOrder.getOrderState());
    }
}
