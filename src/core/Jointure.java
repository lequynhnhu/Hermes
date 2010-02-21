package core;

import java.util.HashSet;
import java.util.Set;

import adaptors.Adaptor;
import adaptors.MySql.SqlBuilder;

public class Jointure extends Hermes {

	private int	parentId;
	private int	childId;

	// Constructors
	public Jointure() {}

	public Jointure(Hermes model, String attribute) {
		this.tableName = Table.joinTableNameFor(attribute, model);
		Adaptor.get().execute(SqlBuilder.build("jointure", tableName), this);
	}

	public boolean save(int parentId, int childId) {
		this.parentId = parentId;
		this.childId = childId;
		return save();
	}

	public void clear(Hermes parent) {
		delete("parentId=" + parent.getId());
	}

	public static Set<Hermes> objectsFor(String attribute, Hermes object) {
		Jointure jointure = object.getManyToManyAssociations().get(attribute).getJointure();
		Set<Hermes> objects = new HashSet<Hermes>();
		Class<Hermes> klass = Introspector.collectionTypeClass(object, attribute);
		Set<Jointure> jointures = (Set<Jointure>) Finder.find(object.getId(), jointure);
		jointures.remove(null);
		for (Jointure join : jointures) {
			Hermes obj = Finder.find(join.getChildId(), klass);
			objects.add(obj);
		}
		return objects;
	}

	// Getters & setters
	public int getParentId() {
		return parentId;
	}

	public void setParentId(int leftId) {
		this.parentId = leftId;
	}

	public int getChildId() {
		return childId;
	}

	public void setChildId(int rightId) {
		this.childId = rightId;
	}
}
