myfun <- function() {
  for(i in 1:10){
    bal <- 1000
    bet <- 100
    games <- 0
    while(bal > 0 && games < 10){
      games <- games + 1
      x <- craps()
      if(x==0){
        bal <- bal - bet
        bet <- bet * 2
        if( bet > bal ){
          bet <-bal
        }
        res <- "LOST"
      } else {
        bal <- bal + bet
        bet <- 100
        res <- "WON"
      }
      # cat("iter=",games,"bal=",bal,"bet=",bet,"res=",res,"\n")
    }
    cat("Round",i,"Games played=",games,"Final Balance=",bal,"\n")    
  }
}