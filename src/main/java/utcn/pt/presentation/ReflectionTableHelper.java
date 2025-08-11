package utcn.pt.presentation;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;


public final class ReflectionTableHelper {
    private ReflectionTableHelper() {}


    public static <T> DefaultTableModel buildTableModel(List<T> data, String... propsOrder) { //String... ca un array de stringuri
        if (propsOrder == null || propsOrder.length == 0) {
            return buildTableModel(data);
        }
        throw new UnsupportedOperationException(
                "Nu am implementat inca filtrarea propsOrder"
        );
    }


    public static <T> DefaultTableModel buildTableModel(List<T> data) {

        if (data == null || data.isEmpty()) {
            return new DefaultTableModel();
        }
        Class<?> type = data.get(0).getClass();
        Field[] fields = type.getDeclaredFields();

        //numele campurilor
        String[] headers =
                java.util.Arrays.stream(fields)
                        .map(Field::getName)
                        .toArray(String[]::new);

        DefaultTableModel model = new DefaultTableModel(headers, 0);
        for (T item : data) {
            Object[] row = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                try {
                    String prop = fields[i].getName();
                    PropertyDescriptor pd = new PropertyDescriptor(prop, type);
                    Method getter = pd.getReadMethod();
                    row[i] = getter.invoke(item);
                } catch (IntrospectionException
                         | IllegalAccessException
                         | InvocationTargetException ex) {
                    row[i] = null;
                }
            }
            model.addRow(row);
        }
        return model;
    }


    public static <T> void populate(JTable table, List<T> data) {
        table.setModel(buildTableModel(data));
    }
}
