package ch.epfl.imhof.osm;

import java.util.Objects;

import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.Attributes;

public final class OSMNode extends OSMEntity {
	public final static class Builder extends OSMEntity.Builder {
		private PointGeo position;

		public Builder (long id, PointGeo position) {
			super(id);

			this.position = Objects.requireNonNull(position, "position ne doit pas être null");
		}

		public OSMNode build () {
			if (this.isIncomplete())
				throw new IllegalStateException("noeud incomplet");

			return new OSMNode(this.id(), this.position, this.attributes());
		}
	}

	private PointGeo position;

	public PointGeo position () { return this.position; }

	public OSMNode (long id, PointGeo position, Attributes attributes) {
		super(id, attributes);

		this.position = Objects.requireNonNull(position, "position ne doit pas être null");
	}
}
