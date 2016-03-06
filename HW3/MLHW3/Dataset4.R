# Check for the requried packages and install them
if (!require("rpart")) 
{
    install.packages("rpart", repos="http://cran.rstudio.com/") 
    library("rpart")
}

if (!require("e1071")) {
    install.packages("e1071", repos="http://cran.rstudio.com/") 
    library("e1071")
}

if (!require("party")) {
    install.packages("party", repos="http://cran.rstudio.com/") 
    library("party")
    
}

if(!require("class"))
{
    install.packages("class", repos="http://cran.rstudio.com/")
    library("class")
}

if(!require("neuralnet"))
{
    install.packages("neuralnet", repos="http://cran.rstudio.com/")
    library("neuralnet")
}

if(!require("nnet"))
{
    install.packages("nnet", repos="http://cran.rstudio.com/")
    library("nnet")
}

# Read from URL
dataURL<-as.character('http://archive.ics.uci.edu/ml/machine-learning-databases/wine/wine.data')
d<-read.csv(dataURL,header = FALSE,sep = ",")

# Remove NA and ? in dataset
d <- na.omit(d)
d[d=="NA"] <- 0
d[d=="?"] <- 0

# Init arrays to store 10 sample accuracies 
dts<- numeric(10)
svms<- numeric(10)
nbs<- numeric(10)
lrs<- numeric(10)
nns<- numeric(10)

# Class attribute is column 1 ie V1

# Run 10 samples
for(i in 1:10) 
{
    cat("Running sample ",i,"\n")
    
    # Generate Train and Test datasets 80/20 split
    sampleInstances<-sample(nrow(d),size = 0.8*nrow(d))
    trainingData <- d[sampleInstances,]
    testData <- d[-sampleInstances,]
    
    # Decision Tree
    method <-"Decision Tree"
    # Model
    dtmodel <- rpart(as.factor(V1) ~ ., data = trainingData, parms=list(split="information"), method ="class", minsplit=1)
    prunedTree <- prune(dtmodel, cp =0.03)
    # Predict
    dTPredict <- predict(prunedTree, newdata = testData, type = "class")
    # Accuracy
    dTTable <- table(testData$V1, dTPredict)
    dTCorrect <- sum(diag(dTTable))
    dTError <- sum(dTTable)-dTCorrect
    dts[i] <- (dTCorrect / (dTCorrect+dTError))*100
    cat("Method = ", method,", accuracy= ", dts[i],"\n")
    
    # SVM
    method <-"SVM"
    # Model
    svmmodel <- svm(as.factor(V1) ~ ., data = trainingData, method="C-classification")
    # Predict
    sVMPredict <- predict(svmmodel, testData,type = "class")
    # Accuracy
    sVMTable <- table(testData$V1, sVMPredict)
    sVMCorrect <- sum(diag(sVMTable))
    sVMError <- sum(sVMTable)-sVMCorrect
    svms[i] <- (sVMCorrect / (sVMCorrect+sVMError))*100
    cat("Method = ", method,", accuracy= ", svms[i],"\n")
    
    # Naive Bayes
    method <-"Naive Bayes"
    # Model
    nbmodel <- naiveBayes(as.factor(V1) ~ ., data = trainingData,laplace=5)
    # Prediction
    nBPredict<- predict(nbmodel,testData)
    # Accuracy
    nBTable <- table(testData$V1, nBPredict)
    nBCorrect <- sum(diag(nBTable))
    nBError <- sum(nBTable)-nBCorrect
    nbs[i] <- (nBCorrect / (nBCorrect+nBError))*100
    cat("Method = ", method,", accuracy= ", nbs[i],"\n")
    
    # Perceptron
    method <-"Perceptron"
    # Model
    lrmodel <- glm(as.factor(V1)~., data = trainingData, family = "binomial") 
    # Predict
    lRPredict<-predict(lrmodel,testData, type="response")
    threshold=0.85
    lRPrediction<-sapply(lRPredict, FUN=function(x) if (x>threshold) 1 else 0)
    actual<-as.integer(testData$V1)
    # Accuracy
    accuracy <- sum(actual==lRPrediction)/length(actual)
    lrs[i] <- accuracy*100
    cat("Method = ", method,", accuracy= ", lrs[i],"\n")
    
    # Neural Net
    method <-"Neural Net"
    # Model
    nnetmodel <- nnet(as.integer(V1)~., trainingData,size=1)
    # Predict
    pred <- predict(nnetmodel, testData)
    # Accuracy
    neural <- table(pred,testData$V1)
    nns[i] <- sum(diag(neural))/sum(neural) *100
    cat("Method = ", method,", accuracy= ", nns[i],"\n")
    
}

# Writing to file
sink("Output 4.txt")
cat("No. of Instances: ",nrow(d))
cat("\n")
cat("No. of Atrributes: ",ncol(d))
cat("\n")
cat("\nOutput for Wine Dataset: ",dataURL)
cat("\n\nDecision Tree Accuracy Array: ",dts)
cat("\n")
cat("\nSVM Accuracy Array: ",svms)
cat("\n")
cat("\nNaive Bayes Accuracy Array: ",nbs)
cat("\n")
cat("\nLogistic Regression Accuracy Array: ",lrs)
cat("\n")
cat("\nNeural Network Accuracy Array: ",nns)
cat("\n")

# Accuracy of 10 iterations
cat("\nAccuracy for 10 iterations")
dTAvg <- sum(dts)/10
cat("\n\nAverage accuracy of 10 samples in Decision Tree Classifier: ",dTAvg)
cat("\n")

sVMAvg <- sum(svms)/10
cat("\nAverage accuracy of 10 samples in SVM Classifier: ",sVMAvg)
cat("\n")

nBAvg <- sum(nbs)/10
cat("\nAverage accuracy of 10 samples in Naive Bayes Classifier: ",nBAvg)
cat("\n")

lRAvg <- sum(lrs)/10
cat("\nAverage accuracy of 10 samples in Logistic Regression Classifier: ",lRAvg)
cat("\n")

nNAvg <- sum(nns)/10
cat("\nAverage accuracy of 10 samples in Neural Network Classifier: ",nNAvg)
cat("\n")
sink()