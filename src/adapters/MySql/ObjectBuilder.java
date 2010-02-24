package adapters.MySql;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import core.Attribute;
import core.Hermes;
import core.Introspector;

public class ObjectBuilder {

	public static Hermes toObject(ResultSet rs, Class<? extends Hermes> model) {
		try {
			if (rs == null || !rs.next()) return null;
			Hermes object = model.newInstance();
			loadObject(object, rs);
			return object;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Set<Hermes> toObjects(ResultSet rs, Class<? extends Hermes> model) {
		Set<Hermes> result = new HashSet<Hermes>();
		Hermes obj = null;
		do {
			obj = toObject(rs, model);
			if (obj != null) result.add(obj);
		} while (obj != null);
		return result;
	}

	// Private methods
	private static void loadAttributeValue(Hermes object, ResultSet rs, Attribute attribute) {
		try {
			Field field = Introspector.fieldFor(object, attribute);
			field.setAccessible(true);
			field.set(object, rs.getObject(field.getName()));
		}
		catch (Exception e) {}
	}

	private static void loadAttributesValue(Hermes object, ResultSet rs) {
		try {
			for (Attribute attribute : object.getAttributes())
				loadAttributeValue(object, rs, attribute);
			object.setId((Integer) rs.getObject("id"));
		}
		catch (SQLException e) {
			// No id column in table
		}
	}

	private static Hermes loadObject(Hermes object, ResultSet rs) {
		object.loadAttributes();
		loadAttributesValue(object, rs);
		return object;
	}
}