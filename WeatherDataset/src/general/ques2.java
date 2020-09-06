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

public class ques2 {
	
	public static class MapForColdOrHotDay extends Mapper<LongWritable,Text,Text,FloatWritable>{
		
		public void map(LongWritable key,Text value, Context con)throws IOException, InterruptedException{

			String line = value.toString();
			String date = line.substring(6, 14).replaceAll(" ", "");
			String HighTemp = line.substring(38, 45).replaceAll(" ", "");
			String LowTemp = line.substring(46, 53).replaceAll(" ", "");
			Float HighTemp2 = Float.parseFloat(HighTemp);
			Float LowTemp2 = Float.parseFloat(LowTemp);
			FloatWritable minTempValue = new FloatWritable(LowTemp2);
			FloatWritable maxTempValue = new FloatWritable(HighTemp2);

			if (LowTemp2 < 10) {
				con.write(new Text("ColdDay " + date), minTempValue);
			}
			if (HighTemp2 > 35) {
				con.write(new Text("HotDay " + date), maxTempValue);
			}

		}
	}
	public static class ReduceForColdOrHotDay extends Reducer<Text,FloatWritable,Text,FloatWritable>{
		public void reduce(Text word, Iterable<FloatWritable> values,Context con) throws IOException, InterruptedException{
			float temp = 0;
			for (FloatWritable value : values) {
				temp = value.get();

			}
			con.write(new Text(word), new FloatWritable(temp));
		}

	}
	
	public static void main(String[] args)throws IOException, ClassNotFoundException, InterruptedException{
		Configuration c = new Configuration();
		Job j = Job.getInstance(c,"Cold Or Hot Day");
		j.setJarByClass(ques2.class);
		j.setMapperClass(MapForColdOrHotDay.class);
		j.setReducerClass(ReduceForColdOrHotDay.class);
		j.setOutputKeyClass(Text.class);
		j.setOutputValueClass(FloatWritable.class);
		FileInputFormat.addInputPath(j,new Path(args[0]));
		FileOutputFormat.setOutputPath(j,new Path(args[1]));
		System.exit(j.waitForCompletion(true)?0:1);
	}
	

}
