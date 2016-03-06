echo "\nEnter dataset between 1-5:"
read choice

if [ $choice == 1 ]; then
	Rscript Dataset1.R
elif [ $choice == 2 ]; then
	Rscript Dataset2.R
elif [ $choice == 3 ]; then
	Rscript Dataset3.R
elif [ $choice == 4 ]; then
	Rscript Dataset4.R
elif [ $choice == 5 ]; then
	Rscript Dataset5.R
else
	for (( i=1; i<=5; i++ ))
	do
		echo 'Executing script Dataset' $i
		Rscript Dataset$i.R
	done
fi