export interface Page<T> {
  pageInfo: PageInfo;
  edges: Edge<T>[];
}

export interface PageInfo {
  hasPreviousPage: boolean;
  hasNextPage: boolean;
  startCursor: string;
  endCursor: string;
}

export interface Edge<T> {
  cursor: string;
  node: T;
}
