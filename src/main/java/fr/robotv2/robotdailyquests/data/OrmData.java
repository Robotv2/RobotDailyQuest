package fr.robotv2.robotdailyquests.data;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class OrmData<D, ID> {

    private ConnectionSource source;
    private Dao<D, ID> dao;

    public void initialize(@NotNull ConnectionSource source, @NotNull Class<D> clazz) throws SQLException {
        this.source = source;
        this.dao = DaoManager.createDao(source, clazz);
        TableUtils.createTableIfNotExists(source, clazz);
    }

    public void closeConnection() {
        this.source.closeQuietly();
    }

    public List<D> getValues() throws Exception {
        final LinkedList<D> result = new LinkedList<>();
        try (CloseableIterator<D> iterator = getDao().closeableIterator()) {
            while (iterator.hasNext()) {
                final D value = iterator.next();
                result.add(value);
            }
        }

        return result;
    }

    public D get(ID identification) {

        try {
            return dao.queryForId(identification);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void remove(D data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(D data) {
        try {
            dao.createOrUpdate(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<D, ID> getDao() {
        return dao;
    }
}
