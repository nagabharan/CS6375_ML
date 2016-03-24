starttime <- Sys.time()

# Import the library
if (!require("RTextTools")) 
{
    install.packages("RTextTools", repos="http://cran.rstudio.com/") 
	library("RTextTools")
}

if (!require("SparseM")) 
{
    install.packages("SparseM", repos="http://cran.rstudio.com/") 
	library("SparseM")
}

# Read the training and test dataset from 5 different classes and combine them
traindata<-read_data("traindataset/",type = "folder", index = "trainlabels.csv", warn=F)
testdata<-read_data("testdataset/",type = "folder", index = "testlabels.csv", warn=F)
combined <- rbind(traindata,testdata)

# Create a DTM and a container
doc_matrix <- create_matrix(combined$Text.Data, language="english", removeNumbers=TRUE, stemWords=TRUE, removePunctuation = TRUE, removeSparseTerms=.998)
container <- create_container(doc_matrix, combined$Labels,trainSize = 1:2966,testSize = 2967:4941, virgin=FALSE)

# Create Models
SVM <- train_model(container,"SVM")
GLMNET <- train_model(container,"GLMNET")
MAXENT <- train_model(container,"MAXENT")
BOOSTING <- train_model(container,"BOOSTING")
# BAGGING <- train_model(container,"BAGGING")
# RF <- train_model(container,"RF")
# SLDA <- train_model(container,"SLDA")
TREE <- train_model(container,"TREE")
# NNET <- train_model(container,"NNET")

# Run the classifiers
SVM_CLASSIFY <- classify_model(container, SVM)
GLMNET_CLASSIFY <- classify_model(container, GLMNET)
MAXENT_CLASSIFY <- classify_model(container, MAXENT)
BOOSTING_CLASSIFY <- classify_model(container, BOOSTING)
# BAGGING_CLASSIFY <- classify_model(container, BAGGING)
# RF_CLASSIFY <- classify_model(container, RF)
# SLDA_CLASSIFY <- classify_model(container, SLDA)
TREE_CLASSIFY <- classify_model(container, TREE)
# NNET_CLASSIFY <- classify_model(container, NNET)

# Generate Analytics
analytics <- create_analytics(container, cbind(SVM_CLASSIFY,GLMNET_CLASSIFY,MAXENT_CLASSIFY,TREE_CLASSIFY,BOOSTING_CLASSIFY))

# Printing Analytics
summary(analytics)

endtime <- Sys.time()

# Printing runtime
print(endtime-starttime)