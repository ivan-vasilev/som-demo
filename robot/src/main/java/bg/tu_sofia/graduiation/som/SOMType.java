package bg.tu_sofia.graduiation.som;

public enum SOMType {
	MATRIX,
	LINEAR;

	public static Integer getDimensions(SOMType type) {
		if (type.equals(SOMType.LINEAR)) {
			return 1;
		} else if (type.equals(SOMType.MATRIX)) {
			return 2;
		}

		return null;
	}
}
