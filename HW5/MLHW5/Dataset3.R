rm(list = ls())
# Check for the requried packages and install them
if(!require("rpart"))
{
    install.packages("rpart")
    library("rpart")
}

if (!require("randomForest")) {
    install.packages("randomForest") 
    library("randomForest")
}

if(!require("kknn"))
{
    install.packages("kknn")
    library("kknn")
}

if(!require("ipred"))
{
    install.packages("ipred")
    library("ipred")
}

if(!require("ada"))
{
    install.packages("ada")
    library("ada")
}

if(!require("gbm"))
{
    install.packages("gbm")
    library("gbm")
}

# Read from URL
dataURL<-as.character('https://archive.ics.uci.edu/ml/machine-learning-databases/breast-cancer-wisconsin/wdbc.data')
d<-read.csv(dataURL,header = FALSE,sep = ",")

# Remove NA and ? in dataset
d <- na.omit(d)
d[d=="NA"] <- 0
d[d=="?"] <- 0

# Init arrays to store 10 sample accuracies 
kNNArray<- numeric(10)
baggingArray<- numeric(10)
rFArray<- numeric(10)
boostingArray<- numeric(10)
gbmArray<- numeric(10)

# Class attribute is column 2 ie V2

# Run 10 samples
for(i in 1:10) 
{
    cat("Running sample ",i,"\n")
    
    # Generate Train and Test datasets 70/30 split
    sampleInstances<-sample(nrow(d),size = 0.7*nrow(d))
    trainingData <- d[sampleInstances,]
    testData <- d[-sampleInstances,]
    
    # KNN
    method <- "kNN"
    # Model
    knn.model <- kknn(formula = formula(V2~.),train=trainingData,test=testData,k = 3,distance=1)
    # Predict
    fit <- fitted(knn.model)
    tab3<-table(testData$V2, fit)
    sum3=tab3[1,2]+tab3[2,1]
    sum4=tab3[1,1]+tab3[1,2]+tab3[2,1]+tab3[2,2]
    # Accuracy
    kNNArray[i] = ((1-(sum3/sum4))*100)
    cat("Method = ", method,", accuracy= ", kNNArray[i],"\n")
    
    # BAGGING
    method <- "Bagging"
    # Model 
    control <- rpart.control(cp=-1, maxdepth=1, minsplit=0)
    bagTrainModel <- bagging(V2~., data=trainingData, control=control, nbagg=10, coob=TRUE)
    # Predict
    bagPredict <-predict(bagTrainModel, testData)
    # Accuracy
    baggingTable <- table(testData$V2, bagPredict)
    baggingCorrect <- sum(diag(baggingTable))
    baggingError <- sum(baggingTable)-baggingCorrect
    baggingArray[i]<- (baggingCorrect / (baggingCorrect+baggingError))*100
    cat("Method = ", method,", accuracy= ", baggingArray[i],"\n")
    
    # RANDOM FOREST
    method <- "Random Forest"
    # Model 
    rFTrainmodel <- randomForest(as.factor(V2) ~ ., data=trainingData, ntree=500, keep.forest=TRUE, importance=TRUE)
    # Predict
    rFPredict <- predict(rFTrainmodel,testData)
    # Accuracy
    rFTable <- table(testData$V2, rFPredict)
    rFCorrect <- sum(diag(rFTable))
    rFError <- sum(rFTable)-rFCorrect
    rFArray[i] <- (rFCorrect / (rFCorrect+rFError))*100
    cat("Method = ", method,", accuracy= ", rFArray[i],"\n")
    
    # BOOSTING
    method <- "Boosting"
    # Model
    boostingTrainModel <- ada(V2 ~ ., data = trainingData, type="discrete")
    # Predict
    boostingPredict=predict(boostingTrainModel,testData)
    # Accuracy
    boostingTable <- table(testData$V2, boostingPredict)
    boostingCorrect <- sum(diag(boostingTable))
    boostingError <- sum(boostingTable)-boostingCorrect
    boostingArray[i]<- (boostingCorrect / (boostingCorrect+boostingError))*100
    cat("Method = ", method,", accuracy= ", boostingArray[i],"\n")
    
    # GBM BOOSTING
    method <- "GBM Boosting"
    # Model
    trainingData$V2 = ifelse(trainingData$V2 == "2", 1, 0)
    testData$V2 = ifelse(testData$V2 == "2", 1, 0)
    # Model
    gbmTrainModel <- gbm(V2~ ., data=trainingData, dist="bernoulli", n.tree = 400,shrinkage = 1, train.fraction = 1)
    # Predict
    gbmPredict=predict(gbmTrainModel,testData,n.trees = 400)
    # Accuracy
    gbmTable <- table(testData$V2>0, gbmPredict>0)
    gbmCorrect <- sum(diag(gbmTable))
    gbmError <- sum(gbmTable)-gbmCorrect
    gbmArray[i]<- (gbmCorrect / (gbmCorrect+gbmError))*100
    cat("Method = ", method,", accuracy= ", gbmArray[i],"\n")
    
}

# Writing to file
sink("Output 3.txt")
cat("No. of Instances: ",nrow(d))
cat("\n")
cat("No. of Atrributes: ",ncol(d))
cat("\n")
cat("\nOutput for ILP Dataset: ",dataURL)
cat("\n\nKNN Accuracy Array: ",kNNArray)
cat("\n")
cat("\nBaggin Accuracy Array: ",baggingArray)
cat("\n")
cat("\nBoosting Accuracy Array: ",boostingArray)
cat("\n")
cat("\nRandom Forest Accuracy Array: ",rFArray)
cat("\n")
cat("\nGradient Boosting Accuracy Array: ",gbmArray)
cat("\n")

# Accuracy of 10 iterations
cat("\nAccuracy for 10 iterations")
knnAvg <- mean(kNNArray)
cat("\n\nAverage accuracy of 10 samples in kNN Classifier: ",knnAvg)
cat("\n")

bagAvg <- mean(baggingArray)
cat("\nAverage accuracy of 10 samples in Bagging Classifier: ",bagAvg)
cat("\n")

boostAvg <- mean(boostingArray)
cat("\nAverage accuracy of 10 samples in Boosting Classifier: ",boostAvg)
cat("\n")

rFAvg <- mean(rFArray)
cat("\nAverage accuracy of 10 samples in Random Forest Classifier: ",rFAvg)
cat("\n")

gbmAvg <- mean(gbmArray)
cat("\nAverage accuracy of 10 samples in Gradient Boosting Classifier: ",gbmAvg)
cat("\n")
sink()