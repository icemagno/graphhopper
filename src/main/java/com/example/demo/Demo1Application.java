package com.example.demo;

import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.dem.SRTMGL1Provider;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.GPXEntry;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

@SpringBootApplication
public class Demo1Application {
	// http://sisgeodef.defesa.mil.br:8989/route?point=-17.916023%2C-49.21875&point=-22.755921%2C-42.451172&type=json&locale=pt-BR&vehicle=car&weighting=fastest&points_encoded=false&elevation=true&key=
	
	public static void main(String[] args) {
		SpringApplication.run(Demo1Application.class, args);
		
		// Pasta dos arquivos HGT
		SRTMGL1Provider srtm = new SRTMGL1Provider( "H:/srtm/" );
		System.out.println( srtm.getEle( -15.825287,-47.807007 ) ); 		
		
		init();
	}
	
	public static void init(){
		// https://www.programcreek.com/java-api-examples/?api=com.graphhopper.GraphHopper
		
		/*
		 * 			CARGA INICIAL
		*/
		
		String mapName = "brazil-latest";
		//GraphHopperOSM hopper = new GraphHopperOSM();
		//hopper.setOSMFile( "H:/teste-rota/osm/" + mapName + ".osm.pbf" );
		
		GraphHopper hopper = new GraphHopperOSM().forServer();
		hopper.setDataReaderFile( "H:/teste-rota/osm/" + mapName + ".osm.pbf" );		
		
		//hopper.setCHEnabled( true ); 
		hopper.setCHEnabled( false );
		hopper.setEnableCalcPoints( true );
		hopper.setAllowWrites( true );
		hopper.setElevation( true );
		//hopper.setEncodingManager( emCar );
		hopper.setEncodingManager( EncodingManager.create("generic , car", 8) );
		hopper.setGraphHopperLocation("H:/teste-rota/osm/graphs_" + mapName);

		// Pasta dos arquivos HGT
		SRTMGL1Provider srtm = new SRTMGL1Provider( "H:/srtm/" );
		hopper.setElevationProvider( srtm );
		
		System.out.println("Preparando...");
		hopper.importOrLoad();
		System.out.println("Fim da preparacao.");

		/*
		*			TESTES
		*/
		// https://github.com/graphhopper/directions-api-clients/tree/master/java-examples/src/main/java/com/graphhopper/directions/api/examples
		// https://github.com/graphhopper/directions-api-java-client/tree/master/client-examples/src/main/java/com/graphhopper/directions/api/examples
		// https://github.com/graphhopper/graphhopper/tree/master/client-hc
		// https://github.com/graphhopper/graphhopper-navigation/tree/master/src/main/java/com/graphhopper/navigation
		// https://github.com/graphhopper/graphhopper/blob/master/reader-osm/src/test/java/com/graphhopper/GraphHopperIT.java
		
		GHRequest request = new GHRequest();
		
		//request.getHints().put( Routing.INSTRUCTIONS, "true" );
		request.getHints().put( "elevation", true );
		// note: turn off instructions and calcPoints if you just need the distance or time 
		// information to make calculation and transmission faster		
		request.getHints().put( "instructions", true );
		request.getHints().put( "calc_points", true );
		
		//request.getHints().put( Parameters.Routing.BLOCK_AREA, "-22.81910709638592,-43.35776925086975,400" );
		
		//request.setVehicle("generic").setWeighting("generic");
		request.setVehicle("car").setWeighting("fastest");
		
		
		/*
		Locale[] locs = Locale.getAvailableLocales();
		for( Locale loca : locs ) {
			System.out.println( loca.toString() );
		}
		*/
		
		request.setLocale( "pt_BR" );

		GHPoint from = new GHPoint( -22.777923,-43.361235 );
		GHPoint to = new GHPoint( -22.845884,-43.361149 );
		
		request.addPoint( from );
		request.addPoint( to );
		
		
		System.out.println("Calculando a rota...");
		GHResponse response =  hopper.route(request);
		
		// use the best path, see the GHResponse class for more possibilities.
		PathWrapper path = response.getBest();

		// points, distance in meters and time in millis of the full path
		PointList pointList = path.getPoints();
		//double maxSubida = path.getAscend();
		//double maxDescida = path.getDescend();
		//double distance = path.getDistance();
		//long timeInMs = path.getTime();
		//path.calcBBox2D();
		
		
		System.out.println( pointList );
		
		InstructionList il = path.getInstructions();
		/*
		for(Instruction instruction : il) {
		   instruction.getDistance();
		   System.out.println( instruction );
		}
		*/

		// or get the json
		List<Map<String, Object>> iList = il.createJson();
		for( Map<String, Object> obj : iList ) {
			System.out.println("-------------------------------------");
			for ( Map.Entry<String, Object> entry : obj.entrySet() ) {
			    System.out.println(entry.getKey() + "  | = |  " + entry.getValue() );
			}			
			System.out.println("-------------------------------------");
		}
		
		//System.out.println( iList );
		
		// or get the result as gpx entries:
		
		List<GPXEntry> list = il.createGPXList();
		System.out.println( list );

		System.out.println("Fim.");
		
		
	}	
		

}
