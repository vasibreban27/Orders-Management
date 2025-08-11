package utcn.pt.DataAccess;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.*;
import utcn.pt.Connection.ConnectionFactory;

public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
    private final Class<T> type; //tipul clasei generice

    @SuppressWarnings("unchecked")
    public AbstractDAO() {  //constructor,determina ce tip concret T a fost folosit in clasa ce extinde AbstractDAo
        this.type = (Class<T>)
                ((ParameterizedType)getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0];
    }

    private String getPrimaryKeyName() {
        String simple = type.getSimpleName();               // ex "Client"
        return Character.toLowerCase(simple.charAt(0))
                + simple.substring(1) + "Id";                  // -> "clientId"
    }

    public List<T> findAll() { //returneaza toate randurile din tabela type
        String sql = "SELECT * FROM " + type.getSimpleName();
        try (Connection conn = ConnectionFactory.getConnection();
             Statement st   = conn.createStatement();
             ResultSet rs   = st.executeQuery(sql)) {
            return createObjects(rs);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public T findById(int id) { //cauta un singur obiect T dupa valoarea cheii primare
        String pk  = getPrimaryKeyName();
        String sql = "SELECT * FROM " + type.getSimpleName()
                + " WHERE " + pk + " = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                return createObjects(rs).stream().findFirst().orElse(null);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
            return null;
        }
    }

    private List<T> createObjects(ResultSet rs) throws SQLException {
        // find no‑arg constructor
        Constructor<T> ctor = Stream.of(type.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0)
                .map(c -> (Constructor<T>)c)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No no‑arg constructor for " + type.getName()));

        List<T> list = new ArrayList<>();
        try {
            ctor.setAccessible(true);
            Field[] fields = type.getDeclaredFields();
            while (rs.next()) { //parcurgere fiecare rand din resultset
                // instantiaza T
                T instance = ctor.newInstance();

                // pt fiecare camp din clasa
                for (Field field : fields) {
                    String name = field.getName();
                    Object raw  = field.getType().equals(Timestamp.class)
                            ? rs.getTimestamp(name)
                            : rs.getObject(name);
                    //obtin setter-ul prin property descriptor
                    PropertyDescriptor pd = new PropertyDescriptor(name, type);
                    pd.getWriteMethod().invoke(instance, raw);
                }
                list.add(instance);
            }
        } catch (InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException
                 | IntrospectionException e) {
            throw new RuntimeException("Error mapping " + type.getName(), e);
        }
        return list;
    }

    public T insert(T obj) { //inserare obiect in baza de date
        Field[] fields = type.getDeclaredFields();
        // build liste de coloane si parametri cu streamuri
        String cols = Stream.of(fields)
                .map(Field::getName)
                .collect(Collectors.joining(", "));
        String vals = Stream.of(fields)
                .map(f -> "?")
                .collect(Collectors.joining(", "));

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                type.getSimpleName(), cols, vals);

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            // parcurg campurile si leg valorile la statement
            IntStream.range(0, fields.length).forEach(i -> {
                try {
                    String name = fields[i].getName();
                    PropertyDescriptor pd = new PropertyDescriptor(name, type);
                    Object value = pd.getReadMethod().invoke(obj);

                    if (value instanceof Timestamp ts) {
                        st.setTimestamp(i+1, ts);
                    } else {
                        st.setObject(i+1, value);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            st.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
        }
        return obj;
    }

    public T update(T obj) { //actualizare rand existent dat de cheia primara
        Field[] fields = type.getDeclaredFields();
        String pk = getPrimaryKeyName();

        // build "col1 = ?, col2 = ?, ..." skipping primary key
        String setClause = Stream.of(fields)
                .map(Field::getName)
                .filter(name -> !name.equals(pk))
                .map(name -> name + " = ?")
                .collect(Collectors.joining(", "));

        String sql = String.format("UPDATE %s SET %s WHERE %s = ?",
                type.getSimpleName(), setClause, pk);

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            // bind all non‑pk fields
            int idx = 1;
            for (Field f : fields) {
                String name = f.getName();
                if (!name.equals(pk)) {
                    PropertyDescriptor pd = new PropertyDescriptor(name, type);
                    Object value = pd.getReadMethod().invoke(obj);
                    if (value instanceof Timestamp ts) {
                        st.setTimestamp(idx++, ts);
                    } else {
                        st.setObject(idx++, value);
                    }
                }
            }
            //then bind the pk
            PropertyDescriptor pdPk = new PropertyDescriptor(pk, type);
            Object pkVal = pdPk.getReadMethod().invoke(obj);
            st.setObject(idx, pkVal);

            st.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
        }
        return obj;
    }

    public void delete(int id) { //stergere rand corespunzator cheii primares
        String pk  = getPrimaryKeyName();
        String sql = "DELETE FROM " + type.getSimpleName()
                + " WHERE " + pk + " = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
        }
    }
}
