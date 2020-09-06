package general;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ques1 {
	
	public static class MapForMaxTempYear extends Mapper<LongWritable,Text,Text,FloatWritable>{
		
		public void map(LongWritable key,Text value, Context con)throws IOException, InterruptedException{
			String line = value.toString();
			String year = line.substring(6, 10).replaceAll(" ", "");
			Text outputkey = new Text(year);
			String HighTemp = line.substring(38, 45).replaceAll(" ", "");
			Float HighTemp2 = Float.parseFloat(HighTemp);
			FloatWritable outputvalue = new FloatWritable(HighTemp2);
			con.write(outputkey, outputvalue);
		}
		
	}
	public static class ReduceForMaxTempYear extends Reducer<Text,FloatWritable,Text,FloatWritable>{
		public void reduce(Text word, Iterable<FloatWritable> values,Context con) throws IOException, InterruptedException{
			float t = 0;
			for (FloatWritable value : values) {
				if (value.get() > t) {
					t = value.get();
				}
			}
			con.write(new Text("Maximum Temperature of " + word
					+ " is "), new FloatWritable(t));
		}

	}
	
	public static void main(String[] args)throws IOException, ClassNotFoundException, InterruptedException{
		Configuration c = new Configuration();
		Job j = Job.getInstance(c,"Max Temp Each Year");
		j.setJarByClass(ques1.class);
		j.setMapperClass(MapForMaxTempYear.class);
		j.setReducerClass(ReduceForMaxTempYear.class);
		j.setOutputKeyClass(Text.class);
		j.setOutputValueClass(FloatWritable.class);
		FileInputFormat.addInputPath(j,new Path(args[0]));
		FileOutputFormat.setOutputPath(j,new Path(args[1]));
		System.exit(j.waitForCompletion(true)?0:1);
	}
	

}
