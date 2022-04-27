export interface Page<T> {
  pageInfo: PageInfo;
  edges: Edge<T>[];
}

export interface PageInfo {
  hasPreviousPage: boolean;
  hasNextPage: boolean;
  startCursor: string | null;
  endCursor: string | null;
}

export interface Edge<T> {
  cursor: string;
  node: T;
}
