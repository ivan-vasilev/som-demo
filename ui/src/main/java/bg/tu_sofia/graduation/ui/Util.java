package bg.tu_sofia.graduation.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

public class Util {

	public static List<Geometry> getShapeFileContent(JFrame frame, String filename)
			throws Exception {
		List<Geometry> geometries = new ArrayList<Geometry>();

		File file = new File(filename);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}

		ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI()
				.toURL());


		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;
		FeatureCollection<SimpleFeatureType, SimpleFeature> collection = null;
		FeatureIterator<SimpleFeature> iterator;

		featureSource = dataStore.getFeatureSource();
		collection = featureSource.getFeatures();

		iterator = collection.features();
		try {
			while (iterator.hasNext()) {

				SimpleFeature feature = iterator.next();

				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				geometries.add(geometry);
			}
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}

		return geometries;
	}
}
