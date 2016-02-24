load.packages('quantmod')

x <- c("DJIA","AAPL","AMZN","BAC","NFLX","PCLN","SPY")

for(i in x){

    data <- getSymbols(i,src="yahoo",from = '2000-01-01',auto.assign = F)

    chartSeries(data,name=i,TA="addSMA(200)",type="line",theme = chartTheme('white.mono'))
    
}
