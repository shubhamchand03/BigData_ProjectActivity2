package general;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ques3 {
	
	public static class MapForTopTen extends Mapper<LongWritable,Text,DoubleWritable,Text>{
		
		public void map(LongWritable key,Text value, Context con)throws IOException, InterruptedException{

			String line = value.toString();
			String date = line.substring(6, 14).replaceAll(" ", "");
			String hightemp = line.substring(38, 45).replaceAll(" ", "");
			String lowtemp = line.substring(46, 53).replaceAll(" ", "");
			double highTemp2 = Double.parseDouble(hightemp);
			double lowTemp2 = Double.parseDouble(lowtemp);
			con.write(new DoubleWritable(lowTemp2), new Text(date));
			con.write(new DoubleWritable(highTemp2), new Text(date));
		}
	}
	public static class ReduceForTopTen extends Reducer<DoubleWritable,Text,DoubleWritable,Text>{
		TreeMap<DoubleWritable, Text> lowTemp = new TreeMap<DoubleWritable, Text>();
		TreeMap<DoubleWritable, Text> highTemp = new TreeMap<DoubleWritable, Text>();
		int i = 0;

		public void reduce(DoubleWritable key, Iterable<Text> values,
				Context con) throws IOException, InterruptedException {

			double temp = key.get();
			String date = values.iterator().next().toString();

			if (i < 10) {
				lowTemp.put(new DoubleWritable(temp), new Text(date));
				++i;
			}

			highTemp.put(new DoubleWritable(temp), new Text(date));
			if (highTemp.size() > 10) {
		
				highTemp.remove(highTemp.keySet().iterator().next());
			}
		}

		public void cleanup(Context con) throws IOException,
				InterruptedException {

			con.write(null, new Text("Top 10 Coldest Days: "));
			for (Map.Entry<DoubleWritable, Text> m : lowTemp.entrySet()) {
				con.write(m.getKey(), m.getValue());
			}

			con.write(null, new Text("Top 10 Hottest Days: "));
			List<DoubleWritable> highTempKeys = new ArrayList<DoubleWritable>(
					highTemp.keySet());
			Collections.reverse(highTempKeys);

			for (int i = 0; i < highTempKeys.size(); i++) {
				con.write(highTempKeys.get(i),
						highTemp.get(highTempKeys.get(i)));
			}
		}

	}

	public static void main(String[] args)throws IOException, ClassNotFoundException, InterruptedException{
		Configuration c = new Configuration();
		Job j = Job.getInstance(c,"Top Ten Hottest and Coldest Day");
		j.setJarByClass(ques3.class);
		j.setMapperClass(MapForTopTen.class);
		j.setReducerClass(ReduceForTopTen.class);
		j.setOutputKeyClass(DoubleWritable.class);
		j.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(j,new Path(args[0]));
		FileOutputFormat.setOutputPath(j,new Path(args[1]));
		System.exit(j.waitForCompletion(true)?0:1);
	}
	

}
