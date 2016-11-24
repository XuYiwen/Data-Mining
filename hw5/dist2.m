function d = dist2(X,C)
    delta = X - C;
    delta = delta(:,2:3);
    d = sum(delta.^2);
end